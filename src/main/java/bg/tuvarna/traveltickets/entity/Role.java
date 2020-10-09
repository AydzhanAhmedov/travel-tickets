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
@Table(name = "roles")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 4074864207273715830L;

    /**
     * Role's names enum.
     */
    public enum Enum { ADMIN, CLIENT }

    @Enumerated(EnumType.STRING)
    @Column(updatable = false, nullable = false)
    private Role.Enum name;

    public Role() {
        super();
    }

    public Role(final Role.Enum name) {
        this.name = name;
    }

    public Role(final Long id, final Role.Enum name) {
        this(name);
        super.id = id;
    }

    public Role.Enum getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return name == role.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

}
