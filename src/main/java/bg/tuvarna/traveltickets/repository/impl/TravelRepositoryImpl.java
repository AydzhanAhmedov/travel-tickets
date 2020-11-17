package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import javax.persistence.EntityManager;
import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.REQUEST_STATUS_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_STATUS_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class TravelRepositoryImpl extends GenericCrudRepositoryImpl<Travel, Long> implements TravelRepository {

    private static final String FIND_ALL_HQL = """
                FROM Travel
            """;

    private static final String FIND_ALL_BY_COMPANY_ID_HQL = """
                SELECT t FROM Travel AS t
                WHERE t.createdBy.id = :userId
            """;

    private static final String FIND_ALL_BY_COMPANY_ID_AND_TRAVEL_STATUS_ID_HQL = """
                SELECT t FROM Travel AS t
                WHERE t.travelStatus.id = :travelStatusId AND t.createdBy.id = :userId
            """;

    private static final String FIND_ALL_BY_DISTRIBUTOR_ID_AND_TRAVEL_STATUS_ID_HQL = """
                SELECT t FROM TravelDistributorRequest AS tdr
                RIGHT JOIN FETCH tdr.travel AS t
                WHERE tdr.requestStatus.id = :requestStatusId AND 
                      t.travelStatus.id = :travelStatusId AND
                      tdr.user.id = :userId
            """;

    private static final String FIND_ALL_DISTRIBUTOR_BY_TRAVEL_ID_HQL = """
                SELECT tdr.user FROM TravelDistributorRequest AS tdr
                WHERE tdr.travel.id = :travelId AND tdr.requestStatus.id = :requestStatusId
            """;

    @Override
    public TravelRoute save(final TravelRoute travelRoute) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();

        if (travelRoute.getTravelCityID() != null) {
            return entityManager.merge(travelRoute);
        }

        entityManager.persist(travelRoute);
        return travelRoute;
    }

    @Override
    public List<Travel> findAll() {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_HQL, Travel.class)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByCompanyId(final Long companyId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_COMPANY_ID_HQL, Travel.class)
                .setParameter(USER_ID_PARAM, companyId)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByCompanyIdAndTravelStatusId(final Long companyId, final Long travelStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_COMPANY_ID_AND_TRAVEL_STATUS_ID_HQL, Travel.class)
                .setParameter(TRAVEL_STATUS_ID_PARAM, travelStatusId)
                .setParameter(USER_ID_PARAM, companyId)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByDistributorIdAndTravelStatusId(final Long distributorId,
                                                                final Long travelStatusId,
                                                                final Long requestStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_DISTRIBUTOR_ID_AND_TRAVEL_STATUS_ID_HQL, Travel.class)
                .setParameter(REQUEST_STATUS_ID_PARAM, requestStatusId)
                .setParameter(TRAVEL_STATUS_ID_PARAM, travelStatusId)
                .setParameter(USER_ID_PARAM, distributorId)
                .getResultList();
    }

    @Override
    public List<User> findAllDistributorsByTravelId(final Long travelId, final Long requestStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_DISTRIBUTOR_BY_TRAVEL_ID_HQL, User.class)
                .setParameter(TRAVEL_ID_PARAM, travelId)
                .setParameter(REQUEST_STATUS_ID_PARAM, requestStatusId)
                .getResultList();
    }

    private static TravelRepositoryImpl instance;

    public static TravelRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (TravelRepositoryImpl.class) {
                if (instance == null) instance = new TravelRepositoryImpl();
            }
        }
        return instance;
    }

    private TravelRepositoryImpl() {
        super();
    }

}
