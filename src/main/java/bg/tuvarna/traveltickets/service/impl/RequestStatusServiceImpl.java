package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.RequestStatus;
import bg.tuvarna.traveltickets.service.RequestStatusService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class RequestStatusServiceImpl implements RequestStatusService {

    private static final Logger LOG = LogManager.getLogger(RequestStatusServiceImpl.class);

    private final List<RequestStatus> requestStatusesCache;
    private final Map<RequestStatus.Enum, RequestStatus> requestStatusByNameCache;

    @Override
    public List<RequestStatus> findAll() {
        return requestStatusesCache;
    }

    @Override
    public RequestStatus findByName(final RequestStatus.Enum requestStatusName) {
        return requestStatusByNameCache.get(Objects.requireNonNull(requestStatusName));
    }

    private static RequestStatusServiceImpl instance;

    public static RequestStatusServiceImpl getInstance() {
        if (instance == null) {
            synchronized (RequestStatusServiceImpl.class) {
                if (instance == null)
                    instance = new RequestStatusServiceImpl();
            }
        }
        return instance;
    }

    private RequestStatusServiceImpl() {
        requestStatusesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM RequestStatus", RequestStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        requestStatusByNameCache = requestStatusesCache.stream()
                .collect(toUnmodifiableMap(RequestStatus::getName, Function.identity()));

        LOG.info("{} instantiated, request statuses fetched and cached.", getClass());
    }

}
