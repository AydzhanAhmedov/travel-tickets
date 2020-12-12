package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.List;

public interface TravelRepository extends GenericCrudRepository<Travel, Long> {

    TravelRoute save(TravelRoute travelRoute);

    TravelDistributorRequest save(TravelDistributorRequest travelDistributorRequest);

    List<TravelDistributorRequest> findAllRequestsByCompanyIdAndRequestStatusId(Long companyId, Long statusId);

    List<TravelDistributorRequest> findAllRequestsByCompanyId(Long companyId);

    List<TravelDistributorRequest> findAllRequestsByDistributorId(Long distributorId);

    List<Travel> findAll();

    List<Travel> findAllByCompanyId(Long companyId);

    List<Travel> findAllByTravelStatusId(Long travelStatusId);

    List<Travel> findAllByCompanyIdAndTravelStatusId(Long companyId, Long travelStatusId);

    List<Travel> findAllByDistributorIdAndTravelStatusId(Long distributorId, Long travelStatusId, Long requestStatusId);

    List<User> findAllDistributorsByTravelId(Long travelId, Long requestStatusId);

}
