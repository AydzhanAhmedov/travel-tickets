package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.repository.CityRepository;
import bg.tuvarna.traveltickets.repository.impl.CityRepositoryImpl;
import bg.tuvarna.traveltickets.service.CityService;
import bg.tuvarna.traveltickets.service.RequestService;

import java.util.List;

public class RequestServiceImpl implements RequestService {

    @Override
    public List<TravelDistributorRequest> findAll() {
        return null;
    }

    private static RequestServiceImpl instance;

    public static RequestServiceImpl getInstance() {
        if (instance == null) {
            synchronized (RequestServiceImpl.class) {
                if (instance == null)
                    instance = new RequestServiceImpl();
            }
        }
        return instance;
    }

    private RequestServiceImpl() {
        super();
    }

}
