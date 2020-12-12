package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.common.AppConfig;
import bg.tuvarna.traveltickets.entity.base.BaseEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serial;
import java.util.Objects;

@Immutable
@Entity
@Table(name = "request_statuses")
public class RequestStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5989003038680325200L;

    public enum Enum {
        PENDING, APPROVED, REJECTED;

        @Override
        public String toString() {
            return AppConfig.getLangBundle().getString("label." + this.name().toLowerCase());
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RequestStatus.Enum name;

    public RequestStatus() {
    }

    public RequestStatus(Enum name) {
        this.name = name;
    }

    public RequestStatus(Long id, Enum name) {
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
        RequestStatus that = (RequestStatus) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
