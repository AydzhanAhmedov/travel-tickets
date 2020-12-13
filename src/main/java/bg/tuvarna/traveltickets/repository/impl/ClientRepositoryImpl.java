package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.Constants.CLIENT_TYPE_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;
import static java.util.stream.Collectors.toMap;

public class ClientRepositoryImpl extends GenericCrudRepositoryImpl<Client, Long> implements ClientRepository {

    private static final String FIND_CLIENT_BY_ID_HQL = """
                SELECT c FROM Client AS c
                LEFT JOIN FETCH c.address AS a
                LEFT JOIN FETCH a.city
                WHERE c.userId = :userId
            """;

    private static final String FIND_RATING_SQL = """
                SELECT user_id, avg(pa)
                FROM
                (
                SELECT t.id ,
                		dclient.user_id ,
                		 count(t2.id) /  CAST(ticket_quantity AS DECIMAL) * 100 AS pa
                FROM travels t
                JOIN tickets t2
                	ON t2.travel_id = t.id
                JOIN cashiers c
                	ON c.client_id = t2.created_by 	
                JOIN distributors d
                	ON d.client_id = c.created_by
                JOIN clients dclient
                	ON dclient.user_id = d.client_id
                WHERE t.travel_status_id = (SELECT id FROM travel_statuses WHERE "name" = 'ENDED')
                GROUP BY t.id , dclient.user_id , t.ticket_quantity
                )
                AS foo
                GROUP BY user_id
            """;

    private static final String FIND_TYPE_BY_ID_HQL = """
                SELECT ct FROM ClientType ct
                RIGHT JOIN Client c ON c.clientType.id = ct.id
                WHERE c.userId = :userId
            """;

    private static final String FIND_ALL_CLIENTS_HQL = """
                SELECT c FROM Client AS c
                LEFT JOIN FETCH c.user
                LEFT JOIN FETCH c.address AS a
                LEFT JOIN FETCH a.city
            """;

    private static final String FIND_ALL_IDS_BY_CLIENT_TYPE_ID_HQL_FORMAT = """
                SELECT c FROM Client AS c
                LEFT JOIN FETCH c.user
                LEFT JOIN FETCH c.address AS a
                LEFT JOIN FETCH a.city
                WHERE c.clientType.id = :clientTypeId
            """;

    private static final String FIND_ALL_CASHIERS_BY_DISTRIBUTOR_ID_HQL = """
                SELECT c FROM Cashier AS c
                LEFT JOIN FETCH c.user
                LEFT JOIN FETCH c.address AS a
                LEFT JOIN FETCH a.city
                WHERE c.createdBy.id IN (:userId)
            """;

    @Override
    public ClientType findTypeByUserId(final Long userId) {
        final TypedQuery<ClientType> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_TYPE_BY_ID_HQL, ClientType.class)
                .setParameter(USER_ID_PARAM, userId);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

    @Override
    public Client findById(final Long userId) {
        final TypedQuery<Client> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_CLIENT_BY_ID_HQL, Client.class)
                .setParameter(USER_ID_PARAM, userId);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

    @Override
    public <T extends Client> T findById(final Class<T> clientClass, final Long userId) {
        return EntityManagerUtil.getEntityManager().find(clientClass, userId);
    }

    @Override
    public List<Client> findAll() {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_CLIENTS_HQL, Client.class)
                .getResultList();
    }

    @Override
    public List<Client> findAllByClientTypeId(final Long clientTypeId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_IDS_BY_CLIENT_TYPE_ID_HQL_FORMAT, Client.class)
                .setParameter(CLIENT_TYPE_ID_PARAM, clientTypeId)
                .getResultList();
    }

    @Override
    public List<Client> findAllCashiersByDistributorIds(final List<Long> distributorId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_CASHIERS_BY_DISTRIBUTOR_ID_HQL, Cashier.class)
                .setParameter(USER_ID_PARAM, distributorId)
                .getResultStream()
                .map(c -> (Client) c)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Long, Integer> findDistributorsRating() {
        final List<Tuple> tuples = EntityManagerUtil.getEntityManager()
                .createNativeQuery(FIND_RATING_SQL, Tuple.class)
                .getResultList();

        return tuples.stream().collect(toMap(t -> t.get(0, Number.class).longValue(), t -> t.get(1, Number.class).intValue()));
    }

    private static ClientRepositoryImpl instance;

    public static ClientRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (ClientRepositoryImpl.class) {
                if (instance == null)
                    instance = new ClientRepositoryImpl();
            }
        }
        return instance;
    }

    private ClientRepositoryImpl() {
        super();
    }

}
