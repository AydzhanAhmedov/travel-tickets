package bg.tuvarna.traveltickets.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "notifications_recipients")
public class NotificationRecipient implements Serializable {

    private static final long serialVersionUID = 268742443330560001L;

    @EmbeddedId
    private NotificationRecipientID notificationRecipientID;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("notificationID")
    private Notification notification;

    // wont work with MapsId  (searchs for column user_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(targetEntity = NotificationStatus.class)
    @JoinColumn(name = "notification_status_id")
    private NotificationStatus notificationStatus;

    public NotificationRecipient() {
    }

    public NotificationRecipient(Notification notification, User user) {
        this.notification = notification;
        this.user = user;
        notificationRecipientID = new NotificationRecipientID(notification.getId(), user.getId());
    }

    public NotificationRecipientID getNotificationRecipientID() {
        return notificationRecipientID;
    }

    public void setNotificationRecipientID(NotificationRecipientID notificationRecipientID) {
        this.notificationRecipientID = notificationRecipientID;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NotificationRecipient notificationRecipient = (NotificationRecipient) o;
        return Objects.equals(notification, notificationRecipient.notification) &&
                Objects.equals(user, notificationRecipient.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notification, user, notification);
    }

    @PrePersist
    protected final void prePersist(){
        if (notificationStatus == null)
            notificationStatus = new NotificationStatus(1L, NotificationStatus.Enum.NOTSEEN);
    }
}

