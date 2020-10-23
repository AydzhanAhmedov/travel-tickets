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
@Table(name = "notification_types")
public class NotificationType extends BaseEntity {

    private static final long serialVersionUID = 7514162606074788391L;

    public enum Enum {NEW_TRAVEL, TRAVEL_STATUS_CHANGED}

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NotificationType.Enum name;

    public NotificationType() {
    }

    public NotificationType(Enum name) {
        this.name = name;
    }

    public NotificationType(Long id, Enum name) {
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
        NotificationType that = (NotificationType) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
