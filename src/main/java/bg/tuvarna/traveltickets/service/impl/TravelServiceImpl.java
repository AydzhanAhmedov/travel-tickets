package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.RequestRepository;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.RequestRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.CityService;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.RequestStatusService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.service.TravelStatusService;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.types.AblyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.NEW_TRAVELS_CHANNEL;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.NEW_TRAVEL;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.TRAVEL_STATUS_CHANGED;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.APPROVED;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.PENDING;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.REJECTED;
import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.INCOMING;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;

public class TravelServiceImpl implements TravelService {

    private static final Logger LOG = LogManager.getLogger(TravelServiceImpl.class);

    private static final Long INCOMING_STATUS_ID = TravelStatusServiceImpl.getInstance().findByName(INCOMING).getId();
    private static final Long DISTRIBUTOR_TYPE_ID = ClientTypeServiceImpl.getInstance().findByName(DISTRIBUTOR).getId();
    private static final Long APPROVED_REQUEST_ID = RequestStatusServiceImpl.getInstance().findByName(APPROVED).getId();
    private static final Long PENDING_REQUEST_ID = RequestStatusServiceImpl.getInstance().findByName(PENDING).getId();

    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();
    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();
    private final RequestRepository requestRepository = RequestRepositoryImpl.getInstance();

    private final AblyRealtime ablyClient = AppConfig.getAblyClient();
    private final AuthService authService = AuthServiceImpl.getInstance();
    private final TravelStatusService travelStatusService = TravelStatusServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();
    private final RequestStatusService requestStatusService = RequestStatusServiceImpl.getInstance();

    private final CityService cityService = CityServiceImpl.getInstance();

    @Override
    public List<Travel> findAll() {
        // admins can see all travels
        if (authService.loggedUserIsAdmin()) {
            return travelRepository.findAll();
        }

        final Long clientId = authService.getLoggedUser().getId();

        // companies only their travels, distributors and cashiers only the travel they can sell tickets
        return switch (authService.getLoggedClientTypeName()) {
            case COMPANY -> travelRepository.findAllByCompanyId(clientId);
            case DISTRIBUTOR -> travelRepository.findAllByTravelStatusId(INCOMING_STATUS_ID);
            case CASHIER -> {
                final Long distributorId = ((Cashier) authService.getLoggedClient()).getCreatedBy().getUserId();
                final Long cityId = authService.getLoggedClient().getAddress().getCity().getId();

                yield travelRepository.findAllByDistributorIdAndTravelStatusId(distributorId, INCOMING_STATUS_ID, APPROVED_REQUEST_ID)
                        .stream()
                        .filter(t -> t.getTravelRoutes().stream().anyMatch(r -> r.getCity().getId().equals(cityId)))
                        .collect(Collectors.toList());
            }
        };
    }

    @Override
    public List<TravelDistributorRequest> findAllRequests() {
        final Long clientId = authService.getLoggedUser().getId();
        final ClientType.Enum clientTypeName = authService.getLoggedClientTypeName();

        return switch (clientTypeName) {
            case DISTRIBUTOR -> travelRepository.findAllRequestsByDistributorId(clientId);
            case COMPANY -> travelRepository.findAllRequestsByCompanyIdAndRequestStatusId(clientId, PENDING_REQUEST_ID);
            default -> throw new RuntimeException("Only distributors and companies are able to view requests!");
        };
    }

    @Override
    public Travel create(final Travel travel) {
        Objects.requireNonNull(travel);
        final List<TravelRoute> travelRoutes = travel.getTravelRoutes();

        if (authService.loggedUserIsAdmin() || authService.getLoggedClientTypeName() != ClientType.Enum.COMPANY) {
            throw new IllegalArgumentException("Logged user must be company, otherwise travels creation is forbidden.");
        }
        if (travelRoutes.size() < 2) {
            throw new IllegalArgumentException("Routes must contain at least 2 cities!");
        }

        travel.setTravelRoutes(travelRoutes.stream()
                .map(t -> {
                    final TravelRoute newRoute = new TravelRoute(travel, cityService.findOrAddByName(t.getCity().getName()));
                    newRoute.setArrivalDate(t.getArrivalDate());
                    newRoute.setTransportType(t.getTransportType());
                    return newRoute;
                }).collect(Collectors.toList()));

        travelRepository.save(travel);
        travelRepository.flush();

        if (AppConfig.ablyIsEnabled()) {
            new Thread(() -> executeInTransaction(em -> createAndSendNewTravelNotifications(travel))).start();
        }

        return travel;
    }

    @Override
    public Travel updateTravel(final Travel travel, final TravelStatus.Enum newStatusName, final String newDetails) {
        if (Objects.requireNonNull(travel).getCreatedBy().getUserId().equals(authService.getLoggedUser().getId())) {
            throw new IllegalArgumentException("Only creator company can edit their travels.");
        }
        if (newStatusName == null && newDetails == null) return travel;

        if (newDetails != null) travel.setDetails(newDetails);
        if (newStatusName != null) travel.setTravelStatus(travelStatusService.findByName(newStatusName));

        travelRepository.save(travel);
        if (newStatusName != null && AppConfig.ablyIsEnabled()) {
            travelRepository.flush();
            // if new status is ended don't send notification
            if (newStatusName == TravelStatus.Enum.ENDED) return travel;
            new Thread(() -> executeInTransaction(em -> createAndSendStatusUpdatedNotifications(travel, newStatusName))).start();
        }

        return travel;
    }

    @Override
    public TravelDistributorRequest createRequest(final Travel travel) {
        if (!DISTRIBUTOR.equals(authService.getLoggedClientTypeName())) {
            throw new RuntimeException("Only distributors can create requests for travels!");
        }
        return travelRepository.save(new TravelDistributorRequest(travel, (Distributor) (authService.getLoggedClient())));
    }

    @Override
    public void acceptRequest(final TravelDistributorRequest travelDistributorRequest) {
        travelDistributorRequest.setRequestStatus(requestStatusService.findByName(APPROVED));
        requestRepository.save(travelDistributorRequest);
    }

    @Override
    public void declineRequest(final TravelDistributorRequest travelDistributorRequest) {
        travelDistributorRequest.setRequestStatus(requestStatusService.findByName(REJECTED));
        requestRepository.save(travelDistributorRequest);
    }

    private boolean createAndSendNewTravelNotifications(final Travel travel) {
        final String message = getLangBundle().getString("label.notification.new_travel").formatted(travel.getName());
        // fetch all the distributors and create notifications for them
        final List<User> recipients = clientRepository.findAllByClientTypeId(DISTRIBUTOR_TYPE_ID).stream()
                .map(Client::getUser)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) return false;

        notificationService.createAndSend(message, NEW_TRAVEL, recipients, (n, r) -> {
            try {
                ablyClient.channels.get(NEW_TRAVELS_CHANNEL).publish("new", message);
                LOG.info("New message published to {} channel.", NEW_TRAVELS_CHANNEL);
            }
            catch (AblyException e) {
                LOG.error("Error while publishing message to ably: ", e);
            }
        });
        return true;
    }

    private boolean createAndSendStatusUpdatedNotifications(final Travel travel, final TravelStatus.Enum newStatusName) {
        final String newStatus = getLangBundle().getString("label.travel_status_" + newStatusName.toString().toLowerCase());
        final String message = getLangBundle().getString("label.notification.travel_status_changed").formatted(travel.getName(), newStatus);

        // fetch all interested distributors and their cashiers to create notifications for them
        final List<User> distributorRecipients = travelRepository.findAllDistributorsByTravelId(travel.getId(), APPROVED_REQUEST_ID);

        final List<Long> distributorIds = distributorRecipients.stream().map(User::getId).collect(Collectors.toList());
        final List<User> recipients = clientRepository.findAllCashiersByDistributorIds(distributorIds).stream()
                .map(Client::getUser)
                .collect(Collectors.toList());

        recipients.addAll(distributorRecipients);

        if (distributorRecipients.isEmpty()) return false;

        notificationService.createAndSend(message, TRAVEL_STATUS_CHANGED, recipients, (n, r) -> {
            distributorRecipients.forEach(u -> {
                try {
                    final String channel = DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT.formatted(u.getId());
                    ablyClient.channels.get(channel).publish("new", message);
                    LOG.info("New message published to {} channel.", channel);
                }
                catch (AblyException e) {
                    LOG.error("Error while publishing message to ably: ", e);
                }
            });
        });
        return true;
    }

    private static TravelServiceImpl instance;

    public static TravelServiceImpl getInstance() {
        if (instance == null) {
            synchronized (TravelServiceImpl.class) {
                if (instance == null)
                    instance = new TravelServiceImpl();
            }
        }
        return instance;
    }

    private TravelServiceImpl() {
        super();
    }

}
