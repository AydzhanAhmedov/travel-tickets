package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.TravelDistributorID;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.repository.RequestRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;

import java.util.List;

public class RequestRepositoryImpl extends GenericCrudRepositoryImpl<TravelDistributorRequest, TravelDistributorID> implements RequestRepository {

    private static RequestRepositoryImpl instance;

    public static RequestRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (RequestRepositoryImpl.class) {
                if (instance == null)
                    instance = new RequestRepositoryImpl();
            }
        }
        return instance;
    }

    private RequestRepositoryImpl() {
        super();
    }

    @Override
    public List<TravelDistributorRequest> findAllByTravelId(final Long travelId) {
        return null;
    }
}
