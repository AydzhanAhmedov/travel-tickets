package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.repository.CityRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;

public class CityRepositoryImpl extends GenericCrudRepositoryImpl<City, Long> implements CityRepository {

    private static CityRepositoryImpl instance;

    public static CityRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (CityRepositoryImpl.class) {
                if (instance == null)
                    instance = new CityRepositoryImpl();
            }
        }
        return instance;
    }

    private CityRepositoryImpl() {
        super();
    }

}
