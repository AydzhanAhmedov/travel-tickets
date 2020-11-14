package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.repository.CityRepository;
import bg.tuvarna.traveltickets.repository.impl.CityRepositoryImpl;
import bg.tuvarna.traveltickets.service.CityService;

public class CityServiceImpl implements CityService {

    CityRepository cityRepository = CityRepositoryImpl.getInstance();

    @Override
    public City findOrAddByName(final String name) {
        City city = cityRepository.findByName(name);

        if (city == null)
            city = cityRepository.save(new City(name));

        return  city;
    }

    private static CityServiceImpl instance;

    public static CityServiceImpl getInstance() {
        if (instance == null) {
            synchronized (CityServiceImpl.class) {
                if (instance == null)
                    instance = new CityServiceImpl();
            }
        }
        return instance;
    }

    private CityServiceImpl() {
        super();
    }

}
