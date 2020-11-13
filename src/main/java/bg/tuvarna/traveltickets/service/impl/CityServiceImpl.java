package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.service.CityService;

public class CityServiceImpl implements CityService {

    @Override
    public City findOrAddByName(final String name) {
        return null;
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
