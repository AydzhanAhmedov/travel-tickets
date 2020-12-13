package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationRecipient;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface NotificationRepository extends GenericCrudRepository<Notification, Long> {

    NotificationRecipient save(NotificationRecipient notificationRecipient);

    List<NotificationRecipient> findAllByRecipientId(Long recipientId);

    List<NotificationRecipient> findAllByRecipientIdAndDateAfter(Long recipientId, OffsetDateTime date);

}
