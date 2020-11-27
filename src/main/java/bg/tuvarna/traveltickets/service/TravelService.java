package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.entity.TravelStatus;

import java.util.List;

public interface TravelService {

    List<Travel> findAll();

    List<TravelDistributorRequest> findAllRequests();

    Travel create(Travel travel);

    Travel updateTravel(Travel travel, TravelStatus.Enum newStatusName, String newDetails);

    TravelDistributorRequest createRequest(Travel travel);

    void acceptRequest(TravelDistributorRequest travelDistributorRequest);

    void declineRequest(TravelDistributorRequest travelDistributorRequest);

}
