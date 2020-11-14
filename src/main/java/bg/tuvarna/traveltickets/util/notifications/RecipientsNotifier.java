package bg.tuvarna.traveltickets.util.notifications;

import java.util.Collection;

/**
 * Interface used to pass reference to a function that implements logic
 * for notifying the recipients for new notifications.
 *
 * @param <N> Notification's type.
 * @param <R> Recipient's type.
 */
@FunctionalInterface
public interface RecipientsNotifier<N, R> {
    void notifyRecipients(Collection<N> notifications, Collection<R> recipients);
}
