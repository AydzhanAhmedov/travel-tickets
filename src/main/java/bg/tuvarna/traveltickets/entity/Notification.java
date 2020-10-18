package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseAuditEntity;
import bg.tuvarna.traveltickets.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification extends BaseAuditEntity {

    private static final long serialVersionUID = 1527812768320401028L;

    //ERROR: null value in column "type_id" of relation "notifications" violates not-null constraint
    @ManyToOne
    @JoinColumn(name = "type_id")
    private NotificationType notificationType;

    @Column(name = "message")
    String message;

    public Notification() {
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Notification notification = (Notification) o;
        return Objects.equals(notificationType,notification.notificationType) &&
                Objects.equals(message, notification.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notificationType, message);
    }
}
