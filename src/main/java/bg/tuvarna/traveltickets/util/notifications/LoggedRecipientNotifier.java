package bg.tuvarna.traveltickets.util.notifications;

import java.util.Collection;

/**
 * Interface used to pass reference to a function that implements logic
 * for notifying the logged user for new notifications.
 *
 * @param <N> the type of the notification.
 */
@FunctionalInterface
public interface LoggedRecipientNotifier<N> {
    void notifyRecipient(Collection<N> notification);
}
