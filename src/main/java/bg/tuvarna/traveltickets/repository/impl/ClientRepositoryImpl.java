package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static bg.tuvarna.traveltickets.common.Constants.CLIENT_TYPE_ID_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class ClientRepositoryImpl extends GenericCrudRepositoryImpl<Client, Long> implements ClientRepository {

    private static final String FIND_CLIENT_BY_ID_HQL = """
                SELECT c FROM Client AS c
                LEFT JOIN FETCH c.address AS a
                LEFT JOIN FETCH a.city
                WHERE c.userId = :userId
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
                SELECT c FROM %s AS c
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
    public <T extends Client> List<Client> findAllByClientTypeId(final Long clientTypeId, final Class<T> clientClass) {
        final String hql = FIND_ALL_IDS_BY_CLIENT_TYPE_ID_HQL_FORMAT.formatted(clientClass.toString());
        return EntityManagerUtil.getEntityManager()
                .createQuery(hql, clientClass)
                .setParameter(CLIENT_TYPE_ID_PARAM, clientTypeId)
                .getResultStream()
                .map(c -> (Client) c)
                .collect(Collectors.toList());
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
