package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.impl.NotificationTypeServiceImpl;

import javax.persistence.PostLoad;

public final class NotificationEntityListener {

    @PostLoad
    public void postLoad(final Notification notification) {
        notification.notificationType = NotificationTypeServiceImpl.getInstance().findById(notification.notificationType.getId());
    }

}
