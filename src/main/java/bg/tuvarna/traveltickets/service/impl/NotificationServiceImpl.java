package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.entity.NotificationStatus;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.NotificationRepository;
import bg.tuvarna.traveltickets.repository.impl.NotificationRepositoryImpl;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import bg.tuvarna.traveltickets.util.notifications.RecipientsNotifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static bg.tuvarna.traveltickets.common.Constants.RECIPIENT_LIST_CANNOT_BE_EMPTY;
import static bg.tuvarna.traveltickets.entity.NotificationStatus.Enum.SEEN;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LogManager.getLogger(NotificationServiceImpl.class);

    private final Map<Long, NotificationType> notificationTypeByIdCache;
    private final Map<Long, NotificationStatus> notificationStatusByIdCache;
    private final Map<NotificationType.Enum, NotificationType> notificationTypeByNameCache;
    private final Map<NotificationStatus.Enum, NotificationStatus> notificationStatusByNameCache;

    private final NotificationRepository notificationRepository = NotificationRepositoryImpl.getInstance();

    @Override
    public Notification create(final String message, final NotificationType.Enum typeName, final List<User> recipients) {
        return createAndSend(message, typeName, recipients, null);
    }

    @Override
    public Notification createAndSend(final String message,
                                      final NotificationType.Enum typeName,
                                      final List<User> recipients,
                                      final RecipientsNotifier<Notification, User> notifier) {

        Objects.requireNonNull(message);
        Objects.requireNonNull(recipients);

        if (recipients.isEmpty()) {
            throw new IllegalArgumentException(RECIPIENT_LIST_CANNOT_BE_EMPTY);
        }

        final NotificationType notificationType = findTypeByName(typeName);
        final Notification notification = notificationRepository.save(new Notification(message, notificationType));

        recipients.stream()
                .map(u -> new NotificationRecipient(notification, u))
                .forEach(notificationRepository::save);

        notificationRepository.flush();

        if (notifier != null) notifier.notifyRecipients(singleton(notification), recipients);

        return notification;
    }

    @Override
    public List<NotificationRecipient> findAllByRecipientId(final Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

    @Override
    public List<NotificationRecipient> findAllByRecipientIdAndDateAfter(final Long recipientId,
                                                                        final OffsetDateTime date) {
        return notificationRepository.findAllByRecipientIdAndDateAfter(recipientId, date.withOffsetSameInstant(UTC));
    }

    @Override
    public NotificationRecipient markAsSeen(final NotificationRecipient notificationRecipient) {
        notificationRecipient.setNotificationStatus(findStatusByName(SEEN));
        LOG.debug("Marking notification with id '{}' as seen.", notificationRecipient.getNotification().getId());
        return notificationRepository.save(notificationRecipient);
    }

    @Override
    public List<NotificationRecipient> markAsSeen(final List<NotificationRecipient> notificationRecipients) {
        notificationRecipients.forEach(this::markAsSeen);
        return notificationRecipients;
    }

    @Override
    public boolean isSeen(final NotificationRecipient notification) {
        return findStatusByName(SEEN).getId().equals(notification.getNotificationStatus().getId());
    }

    @Override
    public NotificationStatus findStatusById(final Long id) {
        return notificationStatusByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public NotificationType findTypeById(final Long id) {
        return notificationTypeByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public NotificationStatus findStatusByName(final NotificationStatus.Enum notificationStatusName) {
        return notificationStatusByNameCache.get(Objects.requireNonNull(notificationStatusName));
    }

    @Override
    public NotificationType findTypeByName(final NotificationType.Enum notificationTypeName) {
        return notificationTypeByNameCache.get(Objects.requireNonNull(notificationTypeName));
    }

    private static NotificationServiceImpl instance;

    public static NotificationServiceImpl getInstance() {
        if (instance == null) {
            synchronized (NotificationServiceImpl.class) {
                if (instance == null)
                    instance = new NotificationServiceImpl();
            }
        }
        return instance;
    }

    private NotificationServiceImpl() {
        final List<NotificationStatus> statuses = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM NotificationStatus", NotificationStatus.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );
        final List<NotificationType> types = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM NotificationType", NotificationType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        notificationTypeByIdCache = types.stream()
                .collect(toUnmodifiableMap(NotificationType::getId, Function.identity()));

        notificationStatusByIdCache = statuses.stream()
                .collect(toUnmodifiableMap(NotificationStatus::getId, Function.identity()));

        notificationTypeByNameCache = types.stream()
                .collect(toUnmodifiableMap(NotificationType::getName, Function.identity()));

        notificationStatusByNameCache = statuses.stream()
                .collect(toUnmodifiableMap(NotificationStatus::getName, Function.identity()));

        LOG.info("{} instantiated, notification statuses and types fetched and cached.", getClass());
    }

}
