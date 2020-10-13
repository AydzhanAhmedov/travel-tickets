package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "regions")
public class Region extends BaseEntity {

    private static final long serialVersionUID = 3862063068546561252L;

    @Column (nullable = false, unique = true)
    private String name;

    public Region(final String name) {
        this.name = name;
    }

    public Region(final long id, final String name) {
        this(name);
        super.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Region region = (Region) o;
        return Objects.equals(name, region.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),name);
    }
}
