package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.TravelType;
import bg.tuvarna.traveltickets.service.TravelTypeService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class TravelTypeServiceImpl implements TravelTypeService {

    private static final Logger LOG = LogManager.getLogger(TravelTypeServiceImpl.class);

    private final List<TravelType> travelTypesCache;
    private final Map<TravelType.Enum, TravelType> travelTypesByNameCache;

    @Override
    public List<TravelType> findAll() {
        return travelTypesCache;
    }

    @Override
    public TravelType findByName(final TravelType.Enum travelTypeName) {
        return travelTypesByNameCache.get(Objects.requireNonNull(travelTypeName));
    }

    private static TravelTypeServiceImpl instance;

    public static TravelTypeServiceImpl getInstance() {
        if (instance == null) {
            synchronized (TravelTypeServiceImpl.class) {
                if (instance == null)
                    instance = new TravelTypeServiceImpl();
            }
        }
        return instance;
    }

    private TravelTypeServiceImpl() {
        travelTypesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TravelType", TravelType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        travelTypesByNameCache = travelTypesCache.stream()
                .collect(toUnmodifiableMap(TravelType::getName, Function.identity()));

        LOG.info("{} instantiated, travel types fetched and cached.", getClass());
    }

}
