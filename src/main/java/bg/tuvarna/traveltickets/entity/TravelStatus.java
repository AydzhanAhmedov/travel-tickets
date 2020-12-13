package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.AppConfig.getLangBundle;

@Immutable
@Entity
@Table(name = "travel_statuses")
public class TravelStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6993633903199159181L;

    public enum Enum {
        INCOMING, ONGOING, CANCELLED, ENDED;

        @Override
        public String toString() {
            final String text = getLangBundle().getString("label.travel_status_" + this.name().toLowerCase());
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TravelStatus.Enum name;

    public TravelStatus() {
    }

    public TravelStatus(Enum name) {
        this.name = name;
    }

    public TravelStatus(Long id, Enum name) {
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
        TravelStatus that = (TravelStatus) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

}
