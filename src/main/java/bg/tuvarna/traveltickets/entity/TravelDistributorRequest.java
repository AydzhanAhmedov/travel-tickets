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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("travelID")
    private Travel travel;

    @JoinColumn(name = "distributor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("distributorID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_status_id")
    private RequestStatus requestStatus;

    public TravelDistributorRequest() {
    }

    public TravelDistributorRequest(Travel travel, User user) {
        this.travel = travel;
        this.user = user;
        this.travelDistributorID = new TravelDistributorID(travel.getId(), user.getId());
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                Objects.equals(user, that.user) &&
                Objects.equals(requestStatus, that.requestStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelDistributorID, travel, user, requestStatus);
    }
}
