package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.NotificationService;
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

public class TravelServiceImpl implements TravelService {

    private static final Logger LOG = LogManager.getLogger(TravelServiceImpl.class);

    private static final Long DISTRIBUTOR_TYPE_ID = ClientTypeServiceImpl.getInstance().findByName(DISTRIBUTOR).getId();
    private static final Long APPROVED_REQUEST_ID = RequestStatusServiceImpl.getInstance().findByName(APPROVED).getId();

    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();
    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();

    private final AblyRealtime ablyClient = AppConfig.getAblyClient();
    private final AuthService authService = AuthServiceImpl.getInstance();
    private final TravelStatusService travelStatusService = TravelStatusServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    @Override
    public Travel create(final Travel travel) {
        Objects.requireNonNull(travel);

        if (authService.loggedUserIsAdmin() || authService.getLoggedClientTypeName() != ClientType.Enum.COMPANY) {
            throw new IllegalArgumentException("Logged user must be company, otherwise travels creation is forbidden.");
        }
        if (travel.getTravelRoutes().size() < 2) {
            throw new IllegalArgumentException("Routes must contain at least 2 cities!");
        }

        travelRepository.save(travel);
        travelRepository.flush();

        final String message = getLangBundle().getString("label.notification.new_travel").formatted(travel.getDetails());
        final List<User> recipients = clientRepository.findAllByClientTypeId(DISTRIBUTOR_TYPE_ID).stream()
                .map(Client::getUser)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) return travel;

        notificationService.createAndSend(message, NEW_TRAVEL, recipients, (n, r) -> {
            if (AppConfig.ablyIsEnabled()) {
                try {
                    ablyClient.channels.get(NEW_TRAVELS_CHANNEL).publish("new", message);
                    LOG.info("New message published to {} channel.", NEW_TRAVELS_CHANNEL);
                }
                catch (AblyException e) {
                    LOG.error("Error while publishing message to ably: ", e);
                }
            }
        });

        return travel;
    }

    @Override
    public Travel updateTravelStatus(final Travel travel, final TravelStatus.Enum newStatusName) {
        Objects.requireNonNull(newStatusName);
        if (Objects.requireNonNull(travel).getCreatedBy().getId().equals(authService.getLoggedUser().getId())) {
            throw new IllegalArgumentException("Only creator company can edit their travels.");
        }
        travel.setTravelStatus(travelStatusService.findByName(Objects.requireNonNull(newStatusName)));

        travelRepository.save(travel);
        travelRepository.flush();

        if (newStatusName == TravelStatus.Enum.ENDED) return travel;

        final String newStatus = getLangBundle().getString("label.travel_status_" + newStatusName.toString().toLowerCase());
        final String message = getLangBundle().getString("label.notification.travel_status_changed").formatted(travel.getDetails(), newStatus);

        final List<User> distributorRecipients = travelRepository.findAllDistributorsByTravelId(travel.getId(), APPROVED_REQUEST_ID);

        final List<Long> distributorIds = distributorRecipients.stream().map(User::getId).collect(Collectors.toList());
        final List<User> recipients = clientRepository.findAllCashiersByDistributorIds(distributorIds).stream()
                .map(Cashier::getUser)
                .collect(Collectors.toList());

        recipients.addAll(distributorRecipients);

        if (distributorRecipients.isEmpty()) return travel;

        notificationService.createAndSend(message, TRAVEL_STATUS_CHANGED, recipients, (n, r) -> {
            if (AppConfig.ablyIsEnabled()) {
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
            }
        });

        return travel;
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
