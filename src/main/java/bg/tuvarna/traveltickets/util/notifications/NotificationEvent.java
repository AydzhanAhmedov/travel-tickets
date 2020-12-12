package bg.tuvarna.traveltickets.util.notifications;

import javafx.event.Event;
import javafx.event.EventType;

import java.io.Serial;

/**
 * This event should be fired on each new notification to notify the user.
 */
public class NotificationEvent extends Event {

    @Serial
    private static final long serialVersionUID = -1575712363518181833L;

    public static final EventType<NotificationEvent> NEW_NOTIFICATION = new EventType<>(ANY);

    public NotificationEvent() {
        super(NEW_NOTIFICATION);
    }

}
