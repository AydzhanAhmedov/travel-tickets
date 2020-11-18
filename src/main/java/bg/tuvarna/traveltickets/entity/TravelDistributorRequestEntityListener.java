package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.RequestStatusServiceImpl;

import javax.persistence.PostLoad;

public final class TravelDistributorRequestEntityListener {

    @PostLoad
    public void postLoad(final TravelDistributorRequest travelDistributorRequest) {
        final RequestStatus requestStatus = RequestStatusServiceImpl.getInstance().findById(travelDistributorRequest.getRequestStatus().getId());
        travelDistributorRequest.setRequestStatus(requestStatus);
    }

}
