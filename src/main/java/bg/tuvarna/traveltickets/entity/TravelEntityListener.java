package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.TravelStatusServiceImpl;

import javax.persistence.PrePersist;

import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.INCOMING;

public final class TravelEntityListener {

    @PrePersist
    public void prePersist(final Travel travel) {
        if (travel.getTravelStatus() == null)
            travel.setTravelStatus(TravelStatusServiceImpl.getInstance().findByName(INCOMING));
    }

}
