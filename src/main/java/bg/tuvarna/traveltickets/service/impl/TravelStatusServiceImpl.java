package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.service.TravelStatusService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class TravelStatusServiceImpl implements TravelStatusService {

    private static final Logger LOG = LogManager.getLogger(TravelStatusServiceImpl.class);

    private final List<TravelStatus> travelStatusesCache;
    private final Map<TravelStatus.Enum, TravelStatus> travelStatusesByNameCache;

    @Override
    public List<TravelStatus> findAll() {
        return travelStatusesCache;
    }

    @Override
    public TravelStatus findByName(final TravelStatus.Enum travelStatusName) {
        return travelStatusesByNameCache.get(Objects.requireNonNull(travelStatusName));
    }

    private static TravelStatusServiceImpl instance;

    public static TravelStatusServiceImpl getInstance() {
        if (instance == null) {
            synchronized (TravelStatusServiceImpl.class) {
                if (instance == null)
                    instance = new TravelStatusServiceImpl();
            }
        }
        return instance;
    }

    private TravelStatusServiceImpl() {
        travelStatusesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM TravelStatus", TravelStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        travelStatusesByNameCache = travelStatusesCache.stream()
                .collect(toUnmodifiableMap(TravelStatus::getName, Function.identity()));

        LOG.info("{} instantiated, travel statuses fetched and cached.", getClass());
    }

}
