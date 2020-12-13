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
@Table(name = "client_types")
public class ClientType extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4811666361979795492L;

    /**
     * ClientType's names enum.
     */
    public enum Enum {
        COMPANY, DISTRIBUTOR, CASHIER;

        @Override
        public String toString() {
            return AppConfig.getLangBundle().getString("label." + this.name().toLowerCase());
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, nullable = false)
    private ClientType.Enum name;

    public ClientType() {
        super();
    }

    public ClientType(final ClientType.Enum name) {
        this.name = name;
    }

    public ClientType(final Long id, final ClientType.Enum name) {
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
        ClientType that = (ClientType) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

}
