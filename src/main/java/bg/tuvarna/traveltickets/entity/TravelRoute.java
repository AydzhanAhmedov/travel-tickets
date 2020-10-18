package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "travels_routes")
public class TravelRoute implements Serializable {

    private static final long serialVersionUID = 4885712622963495508L;

    @EmbeddedId
    private TravelCityID travelCityID;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("travelID")
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cityID")
    private City city;

    @ManyToOne
    @JoinColumn(name = "transport_type_id", nullable = false)
    private TransportType transportType;

    @Column(name = "arrival_date", nullable = false)
    private OffsetDateTime arrivalDate;

    public TravelRoute() {
    }

    public TravelRoute(Travel travel, City city) {
        this.travel = travel;
        this.city = city;
        this.travelCityID = new TravelCityID(travel.getId(), city.getId());
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public OffsetDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(OffsetDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelRoute travelRoute = (TravelRoute) o;
        return Objects.equals(travel, travelRoute.travel) &&
                Objects.equals(transportType, travelRoute.transportType) &&
                Objects.equals(city, travelRoute.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), travel, transportType, city);
    }
}
