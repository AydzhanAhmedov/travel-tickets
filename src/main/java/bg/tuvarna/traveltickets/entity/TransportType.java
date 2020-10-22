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
@Table(name = "transport_types")
public class TransportType extends BaseEntity {

    private static final long serialVersionUID = -7545179502426967324L;

    public enum Enum {BUSS, AIRPLANE, SHIP, CAR, TRAIN}
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TransportType.Enum name;

    public TransportType() {
    }

    public TransportType(Enum name) {
        this.name = name;
    }

    public TransportType(long id, Enum name) {
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
        TransportType transportType = (TransportType) o;
        return Objects.equals(name, transportType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
