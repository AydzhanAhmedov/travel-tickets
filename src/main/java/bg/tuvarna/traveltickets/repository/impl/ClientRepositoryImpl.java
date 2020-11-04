package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;

import javax.persistence.TypedQuery;
import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class ClientRepositoryImpl extends GenericCrudRepositoryImpl<Client, Long> implements ClientRepository {

    private static final String FIND_TYPE_BY_ID_HQL = """
                SELECT ct FROM ClientType ct
                RIGHT JOIN Client c ON c.clientType.id = ct.id
                WHERE c.userId = :userId
            """;

    private static final String FIND_ALL_CLIENTS = """
                FROM Client
            """;

    @Override
    public ClientType findTypeByUserId(final Long userId) {
        final TypedQuery<ClientType> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_TYPE_BY_ID_HQL, ClientType.class)
                .setParameter(USER_ID_PARAM, userId);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

    @Override
    public <T extends Client> T findById(final Class<T> clientClass, final Long userId) {
        return EntityManagerUtil.getEntityManager().find(clientClass, userId);
    }

    @Override
    public List<Client> findAll() {
        final TypedQuery<Client> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_CLIENTS, Client.class);
        return query.getResultList();
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

    public ClientRepositoryImpl() {
        super();
    }

}
