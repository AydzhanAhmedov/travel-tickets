package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.service.NotificationTypeService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class NotificationTypeServiceImpl implements NotificationTypeService {

    private static final Logger LOG = LogManager.getLogger(NotificationTypeServiceImpl.class);

    private final List<NotificationType> notificationTypesCache;
    private final Map<NotificationType.Enum, NotificationType> notificationTypeByNameCache;

    @Override
    public List<NotificationType> findAll() {
        return notificationTypesCache;
    }

    @Override
    public NotificationType findByName(final NotificationType.Enum notificationTypeName) {
        return notificationTypeByNameCache.get(Objects.requireNonNull(notificationTypeName));
    }

    private static NotificationTypeServiceImpl instance;

    public static NotificationTypeServiceImpl getInstance() {
        if (instance == null) {
            synchronized (NotificationTypeServiceImpl.class) {
                if (instance == null)
                    instance = new NotificationTypeServiceImpl();
            }
        }
        return instance;
    }

    private NotificationTypeServiceImpl() {
        notificationTypesCache = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM NotificationType", NotificationType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        notificationTypeByNameCache = notificationTypesCache.stream()
                .collect(toUnmodifiableMap(NotificationType::getName, Function.identity()));

        LOG.info("{} instantiated, notification types fetched and cached.", getClass());
    }

}
