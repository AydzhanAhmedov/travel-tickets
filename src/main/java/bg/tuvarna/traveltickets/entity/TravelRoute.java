package bg.tuvarna.traveltickets.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Embeddable
@Table(name = "travels_routes")
public class TravelRoute implements Serializable {

    private static final long serialVersionUID = 4885712622963495508L;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    Travel travel;

    @ManyToOne
    @JoinColumn(name = "transport_type_id")
    TransportType transportType;

    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    OffsetDateTime arrivalDate;

    TravelRoute() {
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
