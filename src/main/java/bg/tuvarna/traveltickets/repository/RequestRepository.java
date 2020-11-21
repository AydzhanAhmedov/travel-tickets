package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.TravelDistributorID;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.List;

public interface RequestRepository extends GenericCrudRepository<TravelDistributorRequest, TravelDistributorID> {

    List<TravelDistributorRequest> findAllByTravelId(Long travelId);
}
