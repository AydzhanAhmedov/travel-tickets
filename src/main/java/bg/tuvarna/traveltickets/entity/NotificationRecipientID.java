package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class NotificationRecipientID implements Serializable {

    private static final long serialVersionUID = -7117467286788379816L;

    @Column(name = "notification_id", updatable = false)
    private Long notificationID;

    @Column(name = "recipient_id", updatable = false)
    private Long recipientID;

    public NotificationRecipientID() {
    }

    public NotificationRecipientID(Long notificationID, Long recipientID) {
        this.notificationID = notificationID;
        this.recipientID = recipientID;
    }

    public Long getNotificationID() {
        return notificationID;
    }

    public Long getRecipientID() {
        return recipientID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRecipientID that = (NotificationRecipientID) o;
        return Objects.equals(notificationID, that.notificationID) &&
                Objects.equals(recipientID, that.recipientID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationID, recipientID);
    }
}
