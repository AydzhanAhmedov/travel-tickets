package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;

import java.util.List;

public interface RequestService {

    List<TravelDistributorRequest> findAll();

    TravelDistributorRequest createRequest(Travel travel);

    void acceptRequest(TravelDistributorRequest travelDistributorRequest);

    void declineRequest(TravelDistributorRequest travelDistributorRequest);

    RequestStatus findStatusById(Long id);

    RequestStatus findStatusByName(RequestStatus.Enum requestStatusName);

}
