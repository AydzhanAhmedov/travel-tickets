package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.service.CityService;

public class CityServiceImpl implements CityService {

    CityServiceImpl instance;

    @Override
    public City findOrAddByName(final String name) {
        return null;
    }

    public CityServiceImpl getInstance() {
        if (instance == null) {
            synchronized (CityServiceImpl.class) {
                if (instance == null)
                    instance = new CityServiceImpl();
            }
        }

        return instance;
    }
}
