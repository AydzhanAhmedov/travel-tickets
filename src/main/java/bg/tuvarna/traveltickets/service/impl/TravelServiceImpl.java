package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.TransportType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.CityService;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.TravelService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.types.AblyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.common.Constants.DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.NEW_TRAVELS_CHANNEL;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.NEW_TRAVEL;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.TRAVEL_STATUS_CHANGED;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.APPROVED;
import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.ENDED;
import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.INCOMING;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class TravelServiceImpl implements TravelService {

    private static final Logger LOG = LogManager.getLogger(TravelServiceImpl.class);

    private final Map<Long, TransportType> transportTypesByIdCache;
    private final Map<Long, TravelStatus> travelStatusesByIdCache;
    private final Map<Long, TravelType> travelTypesByIdCache;
    private final Map<TransportType.Enum, TransportType> transportTypesByNameCache;
    private final Map<TravelStatus.Enum, TravelStatus> travelStatusesByNameCache;
    private final Map<TravelType.Enum, TravelType> travelTypesByNameCache;

    private static final Long DISTRIBUTOR_TYPE_ID = ClientServiceImpl.getInstance().findTypeByName(DISTRIBUTOR).getId();
    private static final Long APPROVED_REQUEST_ID = RequestServiceImpl.getInstance().findStatusByName(APPROVED).getId();

    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();
    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();

    private final AblyRealtime ablyClient = AppConfig.getAblyClient();
    private final AuthService authService = AuthServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    private final CityService cityService = CityServiceImpl.getInstance();

    @Override
    public List<Travel> findAll() {
        // admins can see all travels
        if (authService.loggedUserIsAdmin()) {
            return travelRepository.findAll();
        }

        final Long clientId = authService.getLoggedUser().getId();
        final Long incomingStatusId = findStatusByName(INCOMING).getId();

        // companies only their travels, distributors and cashiers only the travel they can sell tickets
        return switch (authService.getLoggedClientTypeName()) {
            case COMPANY -> travelRepository.findAllByCompanyId(clientId);
            case DISTRIBUTOR -> travelRepository.findAllByTravelStatusId(incomingStatusId);
            case CASHIER -> {
                final Long distributorId = ((Cashier) authService.getLoggedClient()).getCreatedBy().getUserId();
                final Long cityId = authService.getLoggedClient().getAddress().getCity().getId();

                yield travelRepository.findAllByDistributorIdAndTravelStatusId(distributorId, incomingStatusId, APPROVED_REQUEST_ID)
                        .stream()
                        .filter(t -> t.getTravelRoutes().stream().anyMatch(r -> r.getCity().getId().equals(cityId)))
                        .collect(Collectors.toList());
            }
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
    public Travel updateTravel(final Long id,
                               final TravelStatus.Enum newStatusName,
                               final String newDetails,
                               final Integer newTicketQuantity) {

        final Travel travel = travelRepository.findById(id);

        if (!travel.getCreatedBy().getUserId().equals(authService.getLoggedUser().getId())) {
            throw new IllegalArgumentException("Only creator company can edit their travels.");
        }
        if (newStatusName == null && newDetails == null && newTicketQuantity == null) return travel;

        if (newDetails != null) travel.setDetails(newDetails);
        if (newStatusName != null) validateAndSetStatus(travel, newStatusName);
        if (newTicketQuantity != null) validateAndSetTicketQuantity(travel, newTicketQuantity);

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
    public TransportType findTransportTypeById(final Long id) {
        return transportTypesByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public TransportType findTransportTypeByName(final TransportType.Enum transportTypeName) {
        return transportTypesByNameCache.get(Objects.requireNonNull(transportTypeName));
    }

    @Override
    public TravelStatus findStatusById(final Long id) {
        return travelStatusesByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public TravelStatus findStatusByName(final TravelStatus.Enum travelStatusName) {
        return travelStatusesByNameCache.get(Objects.requireNonNull(travelStatusName));
    }

    @Override
    public TravelType findTypeById(final Long id) {
        return travelTypesByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public TravelType findTypeByName(final TravelType.Enum travelTypeName) {
        return travelTypesByNameCache.get(Objects.requireNonNull(travelTypeName));
    }

    private void validateAndSetTicketQuantity(final Travel travel, final Integer newTicketQuantity) {
        // check if ticket quantity is valid
        if (travel.getTicketQuantity() > newTicketQuantity)
            throw new IllegalArgumentException("Invalid new ticket quantity!");

        travel.setTicketQuantity(newTicketQuantity);
        travel.setCurrentTicketQuantity(travel.getCurrentTicketQuantity() + (newTicketQuantity - travel.getTicketQuantity()));
    }

    private void validateAndSetStatus(final Travel travel, final TravelStatus.Enum newStatusName) {
        // check if new status is valid
        if (switch (travel.getTravelStatus().getName()) {
            case INCOMING -> newStatusName == ENDED;
            case ONGOING -> newStatusName != ENDED;
            default -> newStatusName != travel.getTravelStatus().getName();
        }) {
            throw new IllegalArgumentException("Invalid new status for travel with id %d, old status: %s to new: %s!"
                    .formatted(travel.getId(), travel.getTravelStatus().getName(), newStatusName));
        }
        travel.setTravelStatus(findStatusByName(newStatusName));
    }

    private boolean createAndSendNewTravelNotifications(final Travel travel) {
        final String message = getLangBundle().getString("label.notification.new_travel").formatted(travel.getName());
        // fetch all the distributors and create notifications for them
        final List<User> recipients = clientRepository.findAllByClientTypeId(DISTRIBUTOR_TYPE_ID).stream()
                .map(Client::getUser)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) return false;
        notificationService.createAndSend(message, NEW_TRAVEL, recipients, (n, r) -> publishMessageToAbly(NEW_TRAVELS_CHANNEL, message));

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

        notificationService.createAndSend(message, TRAVEL_STATUS_CHANGED, recipients,
                (n, r) -> distributorIds.forEach(id -> publishMessageToAbly(DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT.formatted(id), message))
        );
        return true;
    }

    private void publishMessageToAbly(final String channel, final String message) {
        try {
            ablyClient.channels.get(channel).publish("new", message);
            LOG.info("New message published to {} channel.", channel);
        }
        catch (AblyException e) {
            LOG.error("Error while publishing message to ably: ", e);
        }
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
        final List<TransportType> transportTypes = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TransportType", TransportType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );
        final List<TravelStatus> statuses = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TravelStatus", TravelStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );
        final List<TravelType> types = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TravelType", TravelType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        travelTypesByIdCache = types.stream()
                .collect(toUnmodifiableMap(TravelType::getId, Function.identity()));
        travelTypesByNameCache = types.stream()
                .collect(toUnmodifiableMap(TravelType::getName, Function.identity()));

        travelStatusesByIdCache = statuses.stream()
                .collect(toUnmodifiableMap(TravelStatus::getId, Function.identity()));
        travelStatusesByNameCache = statuses.stream()
                .collect(toUnmodifiableMap(TravelStatus::getName, Function.identity()));

        transportTypesByIdCache = transportTypes.stream()
                .collect(toUnmodifiableMap(TransportType::getId, Function.identity()));
        transportTypesByNameCache = transportTypes.stream()
                .collect(toUnmodifiableMap(TransportType::getName, Function.identity()));

        LOG.info("{} instantiated, transport types, statuses and types fetched and cached.", getClass());
    }

}
