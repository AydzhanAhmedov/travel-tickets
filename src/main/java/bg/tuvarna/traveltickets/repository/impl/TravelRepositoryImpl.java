package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;
import bg.tuvarna.traveltickets.entity.TravelRoute;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.hibernate.jpa.QueryHints;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.REQUEST_STATUS_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.TRAVEL_STATUS_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class TravelRepositoryImpl extends GenericCrudRepositoryImpl<Travel, Long> implements TravelRepository {

    private static final String FIND_BY_ID_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
                WHERE t.id = :Id
            """;

    private static final String FIND_ALL_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
            """;

    private static final String FIND_ALL_BY_COMPANY_ID_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
                WHERE t.createdBy.id = :userId
            """;

    private static final String FIND_ALL_BY_COMPANY_ID_AND_TRAVEL_STATUS_ID_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
                WHERE t.travelStatus.id = :travelStatusId AND t.createdBy.id = :userId
            """;

    private static final String FIND_ALL_BY_TRAVEL_STATUS_ID_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
                WHERE t.travelStatus.id = :travelStatusId
            """;

    private static final String FIND_ALL_BY_DISTRIBUTOR_ID_AND_TRAVEL_AND_REQUEST_STATUS_ID_HQL = """
                SELECT DISTINCT t FROM Travel AS t
                LEFT JOIN FETCH t.travelRoutes AS tr
                LEFT JOIN FETCH tr.city
                LEFT JOIN FETCH t.createdBy
                RIGHT JOIN t.distributorRequests tdr
                WHERE tdr.requestStatus.id = :requestStatusId AND
                      t.travelStatus.id = :travelStatusId AND
                      tdr.distributor.id = :userId
            """;

    private static final String FIND_ALL_DISTRIBUTOR_BY_TRAVEL_ID_HQL = """
                SELECT tdr.user FROM TravelDistributorRequest AS tdr
                WHERE tdr.travel.id = :travelId AND tdr.requestStatus.id = :requestStatusId
            """;

    private static final String FIND_ALL_REQUESTS_BY_COMPANY_ID_AND_STATUS_ID_HQL = """
                SELECT tdr FROM TravelDistributorRequest AS tdr
                LEFT JOIN FETCH tdr.travel AS t
                WHERE t.createdBy.id = :userId AND tdr.requestStatus.id =:requestStatusId
            """;

    private static final String FIND_ALL_REQUESTS_BY_DISTRIBUTOR_ID_HQL = """
                SELECT tdr FROM TravelDistributorRequest AS tdr
                WHERE tdr.distributor.id = :userId
            """;

    @Override
    public Travel findById(final Long id) {
        final TypedQuery<Travel> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_ID_HQL, Travel.class)
                .setParameter(ID_PARAM, id)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

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
    public TravelDistributorRequest save(final TravelDistributorRequest travelDistributorRequest) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();

        if (travelDistributorRequest.getTravelDistributorID() != null) {
            return entityManager.merge(travelDistributorRequest);
        }

        entityManager.persist(travelDistributorRequest);
        return travelDistributorRequest;
    }

    @Override
    public List<TravelDistributorRequest> findAllRequestsByCompanyIdAndRequestStatusId(final Long companyId,
                                                                                       final Long statusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_REQUESTS_BY_COMPANY_ID_AND_STATUS_ID_HQL, TravelDistributorRequest.class)
                .setParameter(REQUEST_STATUS_ID_PARAM, statusId)
                .setParameter(USER_ID_PARAM, companyId)
                .getResultList();
    }

    @Override
    public List<TravelDistributorRequest> findAllRequestsByDistributorId(final Long distributorId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_REQUESTS_BY_DISTRIBUTOR_ID_HQL, TravelDistributorRequest.class)
                .setParameter(USER_ID_PARAM, distributorId)
                .getResultList();
    }

    @Override
    public List<Travel> findAll() {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_HQL, Travel.class)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByCompanyId(final Long companyId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_COMPANY_ID_HQL, Travel.class)
                .setParameter(USER_ID_PARAM, companyId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByTravelStatusId(final Long travelStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_TRAVEL_STATUS_ID_HQL, Travel.class)
                .setParameter(TRAVEL_STATUS_ID_PARAM, travelStatusId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByCompanyIdAndTravelStatusId(final Long companyId, final Long travelStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_COMPANY_ID_AND_TRAVEL_STATUS_ID_HQL, Travel.class)
                .setParameter(TRAVEL_STATUS_ID_PARAM, travelStatusId)
                .setParameter(USER_ID_PARAM, companyId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<Travel> findAllByDistributorIdAndTravelStatusId(final Long distributorId,
                                                                final Long travelStatusId,
                                                                final Long requestStatusId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_DISTRIBUTOR_ID_AND_TRAVEL_AND_REQUEST_STATUS_ID_HQL, Travel.class)
                .setParameter(REQUEST_STATUS_ID_PARAM, requestStatusId)
                .setParameter(TRAVEL_STATUS_ID_PARAM, travelStatusId)
                .setParameter(USER_ID_PARAM, distributorId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
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
