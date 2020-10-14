package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "cities")
public class City extends BaseEntity {

    private static final long serialVersionUID = 2398116316143393429L;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    private Region region;

    public City() {
    }

    public City(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    public City(long id, String name, Region region) {
        this(name, region);
        super.id = id;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        City city = (City) o;
        return Objects.equals(name, city.name) &&
                Objects.equals(region, city.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, region);
    }
}
