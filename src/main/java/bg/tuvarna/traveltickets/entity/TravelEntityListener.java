package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.TravelStatusServiceImpl;
import bg.tuvarna.traveltickets.service.impl.TravelTypeServiceImpl;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import static bg.tuvarna.traveltickets.entity.TravelStatus.Enum.INCOMING;

public final class TravelEntityListener {

    @PrePersist
    public void prePersist(final Travel travel) {
        if (travel.getTravelStatus() == null)
            travel.setTravelStatus(TravelStatusServiceImpl.getInstance().findByName(INCOMING));
    }

    @PostLoad
    public void postLoad(final Travel travel) {
        travel.setTravelType(TravelTypeServiceImpl.getInstance().findById(travel.getTravelType().getId()));
        travel.setTravelStatus(TravelStatusServiceImpl.getInstance().findById(travel.getTravelStatus().getId()));
    }

}
