package bg.tuvarna.traveltickets.schedule;

import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;
import bg.tuvarna.traveltickets.service.impl.NotificationServiceImpl;
import bg.tuvarna.traveltickets.service.impl.RequestStatusServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelStatusServiceImpl;
import bg.tuvarna.traveltickets.util.notifications.LoggedRecipientNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.SOLD_TICKETS;
import static bg.tuvarna.traveltickets.entity.NotificationType.Enum.UNSOLD_TICKETS;
import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.APPROVED;
import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.INCOMING;
import static bg.tuvarna.traveltickets.util.JpaOperationsUtil.executeInTransaction;
import static java.util.Collections.singletonList;

/**
 * This class contains logic for performing checks and creating notifications
 * if needed.
 */
public final class ScheduledTicketsNotificationCheck implements Runnable {

    private static final Logger LOG = LogManager.getLogger(ScheduledTicketsNotificationCheck.class);

    private static final Long INCOMING_STATUS_ID = TravelStatusServiceImpl.getInstance().findByName(INCOMING).getId();
    private static final Long APPROVED_STATUS_ID = RequestStatusServiceImpl.getInstance().findByName(APPROVED).getId();

    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final NotificationService notificationService = NotificationServiceImpl.getInstance();

    private final LoggedRecipientNotifier<Notification> notifierFunction;

    public ScheduledTicketsNotificationCheck(final LoggedRecipientNotifier<Notification> notifierFunction) {
        this.notifierFunction = Objects.requireNonNull(notifierFunction);
    }

    @Override
    public void run() {
        if (authService.loggedUserIsAdmin()) {
            LOG.warn("No notification check needs to be performed cause user is admin.");
            return;
        }

        final Long clientId = authService.getLoggedClient().getUserId();
        final ClientType.Enum clientType = authService.getLoggedClientTypeName();

        if (!(clientType == COMPANY || clientType == DISTRIBUTOR)) {
            LOG.warn("No notification check needs to be performed cause user is cashier.");
            return;
        }
        notifierFunction.notifyRecipient(executeInTransaction(em -> createNotificationsIfNeeded(clientId, clientType)));
    }

    private List<Notification> createNotificationsIfNeeded(final Long clientId, final ClientType.Enum clientType) {
        final List<Travel> travels = findTravels(clientId, clientType);
        if (travels.isEmpty()) {
            LOG.debug("No travels found for the client with id '{}', no notifications will be created.", clientId);
            return Collections.emptyList();
        }

        final String soldFormat = getLangBundle().getString("label.notification.sold_tickets");
        final String unsoldFormat = getLangBundle().getString("label.notification.unsold_tickets");

        final List<Notification> notifications = new ArrayList<>();
        if (clientType == COMPANY) {
            // ticketQuantity - currentQuantity = soldTicketsQuantity
            notifications.addAll(createNotifications(travels, t -> soldFormat
                    .formatted(t.getName(), t.getTicketQuantity() - t.getCurrentTicketQuantity()), SOLD_TICKETS));

            LOG.debug("{} notifications of type {} created.", notifications.size(), SOLD_TICKETS.toString());
        }

        final List<Notification> unsoldNotifications = createNotifications(travels, t -> unsoldFormat.formatted(t.getName()), UNSOLD_TICKETS);
        notifications.addAll(unsoldNotifications);

        LOG.debug("{} notifications of type {} created.", unsoldNotifications.size(), UNSOLD_TICKETS.toString());

        return notifications;
    }

    private List<Travel> findTravels(final Long clientId, final ClientType.Enum clientType) {
        // if the client is company, fetch all its incoming travels
        if (clientType == COMPANY)
            return travelRepository.findAllByCompanyIdAndTravelStatusId(clientId, INCOMING_STATUS_ID);

        // if the client is distributor, fetch all the incoming travels he has approved status on
        return travelRepository.findAllByDistributorIdAndTravelStatusId(clientId, INCOMING_STATUS_ID, APPROVED_STATUS_ID);
    }

    private List<Notification> createNotifications(final List<Travel> travels,
                                                   final Function<Travel, String> messageGenerator,
                                                   final NotificationType.Enum typeName) {

        final List<User> recipients = singletonList(authService.getLoggedUser());
        return travels.stream()
                .map(m -> notificationService.create(messageGenerator.apply(m), typeName, recipients))
                .collect(Collectors.toList());
    }

}
