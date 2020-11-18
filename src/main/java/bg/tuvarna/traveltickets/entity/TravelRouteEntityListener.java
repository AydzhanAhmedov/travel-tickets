package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.TransportTypeServiceImpl;

import javax.persistence.PostLoad;

public final class TravelRouteEntityListener {

    @PostLoad
    public void postLoad(final TravelRoute travelRoute) {
        travelRoute.setTransportType(TransportTypeServiceImpl.getInstance().findById(travelRoute.getTransportType().getId()));
    }

}
