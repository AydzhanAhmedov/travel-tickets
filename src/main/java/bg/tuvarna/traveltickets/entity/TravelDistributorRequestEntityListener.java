package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.RequestStatusServiceImpl;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import static bg.tuvarna.traveltickets.entity.RequestStatus.Enum.PENDING;

public final class TravelDistributorRequestEntityListener {

    @PrePersist
    public void prePersist(final TravelDistributorRequest travelDistributorRequest) {
        final RequestStatus requestStatus = RequestStatusServiceImpl.getInstance().findByName(PENDING);
        travelDistributorRequest.setRequestStatus(requestStatus);
    }

    @PostLoad
    public void postLoad(final TravelDistributorRequest travelDistributorRequest) {
        final RequestStatus requestStatus = RequestStatusServiceImpl.getInstance().findById(travelDistributorRequest.getRequestStatus().getId());
        travelDistributorRequest.setRequestStatus(requestStatus);
    }

}
