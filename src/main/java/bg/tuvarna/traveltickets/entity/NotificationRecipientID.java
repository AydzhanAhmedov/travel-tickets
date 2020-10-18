package bg.tuvarna.traveltickets.entity;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class NotificationRecipientID implements Serializable {

    private static final long serialVersionUID = -7117467286788379816L;

    @Column(name = "notification_id")
    private Long notificationID;

    @Column(name = "recipient_id")
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

    public void setNotificationID(Long notificationID) {
        this.notificationID = notificationID;
    }

    public Long getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(Long recipientID) {
        this.recipientID = recipientID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NotificationRecipientID notificationRecipientID = (NotificationRecipientID) o;
        return Objects.equals(notificationID, notificationRecipientID.notificationID) &&
                Objects.equals(recipientID, notificationRecipientID.recipientID);
    }

    @Override
    public int hashCode() {
        return  Objects.hash(super.hashCode(), notificationID, recipientID);
    }
}
