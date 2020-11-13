package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.service.ClientTypeService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class ClientTypeServiceImpl implements ClientTypeService {

    private static final Logger LOG = LogManager.getLogger(ClientTypeServiceImpl.class);

    private final List<ClientType> clientTypesCache;
    private final Map<ClientType.Enum, ClientType> clientTypeByNameCache;

    @Override
    public List<ClientType> findAll() {
        return clientTypesCache;
    }

    @Override
    public ClientType findByName(final ClientType.Enum clientTypeName) {
        return clientTypeByNameCache.get(Objects.requireNonNull(clientTypeName));
    }

    private static ClientTypeServiceImpl instance;

    public static ClientTypeServiceImpl getInstance() {
        if (instance == null) {
            synchronized (ClientTypeServiceImpl.class) {
                if (instance == null)
                    instance = new ClientTypeServiceImpl();
            }
        }

        return instance;
    }

    private ClientTypeServiceImpl() {
        clientTypesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM ClientType", ClientType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        clientTypeByNameCache = clientTypesCache.stream()
                .collect(toUnmodifiableMap(ClientType::getName, Function.identity()));

        LOG.info("{} instantiated, client types fetched and cached.", getClass());
    }

}
