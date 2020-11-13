package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.NotificationRepository;
import bg.tuvarna.traveltickets.repository.impl.NotificationRepositoryImpl;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.NotificationStatusService;
import bg.tuvarna.traveltickets.service.NotificationTypeService;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import java.util.List;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.Constants.RECIPIENT_LIST_CANNOT_BE_EMPTY;
import static bg.tuvarna.traveltickets.entity.NotificationStatus.Enum.SEEN;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository = NotificationRepositoryImpl.getInstance();

    private final NotificationTypeService notificationTypeService = NotificationTypeServiceImpl.getInstance();
    private final NotificationStatusService notificationStatusService = NotificationStatusServiceImpl.getInstance();

    @Override
    public Notification create(final String message,
                               final NotificationType.Enum notificationTypeName,
                               final List<User> recipients) {

        Objects.requireNonNull(message);
        Objects.requireNonNull(recipients);

        if (recipients.isEmpty()) {
            throw new IllegalArgumentException(RECIPIENT_LIST_CANNOT_BE_EMPTY);
        }

        final NotificationType notificationType = notificationTypeService.findByName(notificationTypeName);
        final Notification notification = notificationRepository.save(new Notification(message, notificationType));

        recipients.stream()
                .map(user -> new NotificationRecipient(notification, user))
                .forEach(n -> EntityManagerUtil.getEntityManager().persist(n));

        notifyRecipients(notification, recipients);

        return notification;
    }

    @Override
    public List<NotificationRecipient> findAllByRecipientId(final Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

    @Override
    public NotificationRecipient markAsSeen(final NotificationRecipient notificationRecipient) {
        notificationRecipient.setNotificationStatus(notificationStatusService.findByName(SEEN));
        return notificationRepository.save(notificationRecipient);
    }

    @Override
    public List<NotificationRecipient> markAsSeen(final List<NotificationRecipient> notificationRecipients) {
        notificationRecipients.forEach(this::markAsSeen);
        return notificationRecipients;
    }

    @Override
    public boolean isSeen(final NotificationRecipient notification) {
        return notificationStatusService.findByName(SEEN).getId().equals(notification.getNotificationStatus().getId());
    }

    private void notifyRecipients(final Notification notification, final List<User> recipients) {
        //TODO: implement logic for notifying recipients
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
        super();
    }

}
