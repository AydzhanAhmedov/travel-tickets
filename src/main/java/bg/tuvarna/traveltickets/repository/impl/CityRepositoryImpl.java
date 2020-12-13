package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.repository.CityRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;

import javax.persistence.TypedQuery;

import static bg.tuvarna.traveltickets.common.Constants.CITY_NAME_PARAM;

public class CityRepositoryImpl extends GenericCrudRepositoryImpl<City, Long> implements CityRepository {

    private static final String FIND_CITY_BY_NAME_HQL = """
                SELECT c FROM City c
                WHERE c.name = :cityName
            """;

    @Override
    public City findByName(final String cityName) {
        final TypedQuery<City> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_CITY_BY_NAME_HQL, City.class)
                .setParameter(CITY_NAME_PARAM, cityName);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

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
