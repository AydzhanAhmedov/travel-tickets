package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.TransportType;
import bg.tuvarna.traveltickets.service.TransportTypeService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class TransportTypeServiceImpl implements TransportTypeService {

    private static final Logger LOG = LogManager.getLogger(TransportTypeServiceImpl.class);

    private final List<TransportType> transportTypesCache;
    private final Map<TransportType.Enum, TransportType> transportTypesByNameCache;

    @Override
    public List<TransportType> findAll() {
        return transportTypesCache;
    }

    @Override
    public TransportType findByName(final TransportType.Enum transportTypeName) {
        return transportTypesByNameCache.get(Objects.requireNonNull(transportTypeName));
    }

    private static TransportTypeServiceImpl instance;

    public static TransportTypeServiceImpl getInstance() {
        if (instance == null) {
            synchronized (TransportTypeServiceImpl.class) {
                if (instance == null)
                    instance = new TransportTypeServiceImpl();
            }
        }
        return instance;
    }

    private TransportTypeServiceImpl() {
        transportTypesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TransportType", TransportType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        transportTypesByNameCache = transportTypesCache.stream()
                .collect(toUnmodifiableMap(TransportType::getName, Function.identity()));

        LOG.info("{} instantiated, transport types fetched and cached.", getClass());
    }

}
