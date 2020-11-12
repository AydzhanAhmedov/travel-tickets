package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.NotificationRepository;
import bg.tuvarna.traveltickets.repository.impl.NotificationRepositoryImpl;
import bg.tuvarna.traveltickets.service.NotificationService;
import bg.tuvarna.traveltickets.service.NotificationTypeService;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import java.util.List;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.Constants.RECIPIENT_LIST_CANNOT_BE_EMPTY;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository = NotificationRepositoryImpl.getInstance();
    private final NotificationTypeService notificationTypeService = NotificationTypeServiceImpl.getInstance();

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
