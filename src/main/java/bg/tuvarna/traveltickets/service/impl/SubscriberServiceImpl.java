package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.SubscriberService;
import bg.tuvarna.traveltickets.util.notifications.LoggedRecipientNotifier;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.types.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.Constants.DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.NEW_TRAVELS_CHANNEL;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;

public class SubscriberServiceImpl implements SubscriberService {

    private static final Logger LOG = LogManager.getLogger(SubscriberServiceImpl.class);

    private final AblyRealtime ablyClient = AppConfig.getAblyClient();
    private final AuthService authService = AuthServiceImpl.getInstance();

    @Override
    public void subscribeForNewTravels(final LoggedRecipientNotifier<Message> notifier) {
        if (authService.loggedUserIsAdmin() || authService.getLoggedClientTypeName() != DISTRIBUTOR) {
            LOG.debug("Cannot subscribe to '{}' channel because user is not distributor.", NEW_TRAVELS_CHANNEL);
            return;
        }
        subscribeToChannel(NEW_TRAVELS_CHANNEL, Objects.requireNonNull(notifier));
    }

    @Override
    public void subscribeForDistributorSpecificTravels(final LoggedRecipientNotifier<Message> notifier) {
        if (authService.loggedUserIsAdmin() || authService.getLoggedClientTypeName() == COMPANY) {
            LOG.debug("Cannot subscribe for distributor's travels because user is not distributor or cashier.");
            return;
        }
        subscribeToChannel(DISTRIBUTOR_TRAVELS_CHANNEL_FORMAT.formatted(getDistributorId()), Objects.requireNonNull(notifier));
    }

    private Long getDistributorId() {
        return authService.getLoggedClientTypeName() == DISTRIBUTOR
                ? authService.getLoggedUser().getId() : ((Cashier) authService.getLoggedClient()).getCreatedBy().getUserId();
    }

    private void subscribeToChannel(final String channel, final LoggedRecipientNotifier<Message> notifier) {
        if (AppConfig.ablyIsEnabled()) {
            try {
                ablyClient.channels.get(channel).subscribe(message -> {
                    LOG.info("New notification received with message: {}", message.data);
                    notifier.notifyRecipient(Collections.singleton(message));
                });
                LOG.info("Subscribed to {} channel.", channel);
            }
            catch (Exception e) {
                LOG.error("Error while subscribing to channel: ", e);
            }
        }
    }

    private static SubscriberServiceImpl instance;

    public static SubscriberServiceImpl getInstance() {
        if (instance == null) {
            synchronized (SubscriberServiceImpl.class) {
                if (instance == null)
                    instance = new SubscriberServiceImpl();
            }
        }
        return instance;
    }

    private SubscriberServiceImpl() {
        super();
    }

}
