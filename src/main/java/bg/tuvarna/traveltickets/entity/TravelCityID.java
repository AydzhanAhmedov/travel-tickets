package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TravelCityID implements Serializable {

    private static final long serialVersionUID = 5789773928902544080L;

    @Column(name = "travel_id")
    private Long travelID;

    @Column(name = "city_id")
    private Long cityID;

    public TravelCityID() {
    }

    public TravelCityID(Long travelID, Long cityID) {
        this.travelID = travelID;
        this.cityID = cityID;
    }

    public Long getTravelID() {
        return travelID;
    }

    public void setTravelID(Long travelID) {
        this.travelID = travelID;
    }

    public Long getCityID() {
        return cityID;
    }

    public void setCityID(Long cityID) {
        this.cityID = cityID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelCityID travelCityID = (TravelCityID) o;
        return Objects.equals(travelID, travelCityID.travelID) &&
                Objects.equals(cityID, travelCityID.cityID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), travelID, cityID);
    }
}
