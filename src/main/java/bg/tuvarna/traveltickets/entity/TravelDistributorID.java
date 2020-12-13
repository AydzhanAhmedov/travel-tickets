package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TravelDistributorID implements Serializable {

    @Serial
    private static final long serialVersionUID = 6001228055814622307L;

    @Column(name = "travel_id")
    private Long travelID;

    @Column(name = "distributor")
    private Long distributorID;

    public TravelDistributorID() {
    }

    public TravelDistributorID(Long travelID, Long distributorID) {
        this.travelID = travelID;
        this.distributorID = distributorID;
    }

    public Long getTravelID() {
        return travelID;
    }

    public Long getDistributorID() {
        return distributorID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDistributorID that = (TravelDistributorID) o;
        return Objects.equals(travelID, that.travelID) &&
                Objects.equals(distributorID, that.distributorID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelID, distributorID);
    }
}
