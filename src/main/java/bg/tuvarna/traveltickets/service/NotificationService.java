package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.entity.NotificationStatus;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.util.notifications.RecipientsNotifier;

import java.time.OffsetDateTime;
import java.util.List;

public interface NotificationService {

    Notification create(String message, NotificationType.Enum typeName, List<User> recipients);

    Notification createAndSend(String message,
                               NotificationType.Enum typeName,
                               List<User> recipients,
                               RecipientsNotifier<Notification, User> notifier);

    List<NotificationRecipient> findAllByRecipientId(Long recipientId);

    List<NotificationRecipient> findAllByRecipientIdAndDateAfter(Long recipientId, OffsetDateTime date);

    NotificationRecipient markAsSeen(NotificationRecipient notificationRecipient);

    List<NotificationRecipient> markAsSeen(List<NotificationRecipient> notificationRecipients);

    boolean isSeen(NotificationRecipient notification);

    NotificationStatus findStatusById(Long id);

    NotificationType findTypeById(Long id);

    NotificationStatus findStatusByName(NotificationStatus.Enum notificationStatusName);

    NotificationType findTypeByName(NotificationType.Enum notificationTypeName);

}
