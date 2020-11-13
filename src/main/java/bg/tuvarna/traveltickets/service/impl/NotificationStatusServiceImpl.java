package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.NotificationStatus;
import bg.tuvarna.traveltickets.service.NotificationStatusService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class NotificationStatusServiceImpl implements NotificationStatusService {

    private static final Logger LOG = LogManager.getLogger(NotificationStatusServiceImpl.class);

    private final List<NotificationStatus> notificationStatusesCache;
    private final Map<NotificationStatus.Enum, NotificationStatus> notificationStatusByNameCache;

    @Override
    public List<NotificationStatus> findAll() {
        return notificationStatusesCache;
    }

    @Override
    public NotificationStatus findByName(final NotificationStatus.Enum notificationStatusName) {
        return notificationStatusByNameCache.get(Objects.requireNonNull(notificationStatusName));
    }

    private static NotificationStatusServiceImpl instance;

    public static NotificationStatusServiceImpl getInstance() {
        if (instance == null) {
            synchronized (NotificationStatusServiceImpl.class) {
                if (instance == null)
                    instance = new NotificationStatusServiceImpl();
            }
        }
        return instance;
    }

    private NotificationStatusServiceImpl() {
        notificationStatusesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM NotificationStatus", NotificationStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        notificationStatusByNameCache = notificationStatusesCache.stream()
                .collect(toUnmodifiableMap(NotificationStatus::getName, Function.identity()));

        LOG.info("{} instantiated, notification statuses fetched and cached.", getClass());
    }

}
