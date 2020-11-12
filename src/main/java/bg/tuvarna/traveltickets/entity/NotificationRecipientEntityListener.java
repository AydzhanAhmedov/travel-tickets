package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.NotificationStatusServiceImpl;

import javax.persistence.PrePersist;

import static bg.tuvarna.traveltickets.entity.NotificationStatus.Enum.NOT_SEEN;

public final class NotificationRecipientEntityListener {

    @PrePersist
    public void prePersist(final NotificationRecipient notificationRecipient) {
        if (notificationRecipient.getNotificationStatus() == null)
            notificationRecipient.setNotificationStatus(NotificationStatusServiceImpl.getInstance().findByName(NOT_SEEN));
    }

}
