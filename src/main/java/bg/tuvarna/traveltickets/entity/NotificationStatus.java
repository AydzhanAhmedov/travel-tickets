package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Objects;

@Immutable
@Entity
@Table(name = "notification_statuses")
public class NotificationStatus extends BaseEntity {

    private static final long serialVersionUID = -5904733920359559595L;

    public enum Enum { SEEN, NOT_SEEN }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NotificationStatus.Enum name;

    public NotificationStatus() {
    }

    public NotificationStatus(Enum name) {
        this.name = name;
    }

    public NotificationStatus(Long id, Enum name) {
        this(name);
        super.id = id;
    }

    public Enum getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NotificationStatus notificationStatus = (NotificationStatus) o;
        return Objects.equals(name, notificationStatus.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
