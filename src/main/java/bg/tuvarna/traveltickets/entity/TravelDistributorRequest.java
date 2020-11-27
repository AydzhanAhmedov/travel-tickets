package bg.tuvarna.traveltickets.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@EntityListeners(TravelDistributorRequestEntityListener.class)
@Entity
@Table(name = "travel_distributor_requests")
public class TravelDistributorRequest implements Serializable {

    private static final long serialVersionUID = 6330458813028565516L;

    @EmbeddedId
    private TravelDistributorID travelDistributorID;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("travelID")
    private Travel travel;

    @JoinColumn(name = "distributor_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("distributorID")
    private Distributor distributor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_status_id")
    private RequestStatus requestStatus;

    public TravelDistributorRequest() {
    }

    public TravelDistributorRequest(Travel travel, Distributor distributor) {
        this.travel = travel;
        this.distributor = distributor;
        this.travelDistributorID = new TravelDistributorID(travel.getId(), distributor.getUserId());
    }

    public TravelDistributorID getTravelDistributorID() {
        return travelDistributorID;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelDistributorRequest that = (TravelDistributorRequest) o;
        return Objects.equals(travelDistributorID, that.travelDistributorID) &&
                Objects.equals(travel, that.travel) &&
                Objects.equals(distributor, that.distributor) &&
                Objects.equals(requestStatus, that.requestStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelDistributorID, travel, distributor, requestStatus);
    }
}
