package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.repository.NotificationRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;

public class NotificationRepositoryImpl extends GenericCrudRepositoryImpl<Notification, Long> implements NotificationRepository {

    private static NotificationRepositoryImpl instance;

    public static NotificationRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (NotificationRepositoryImpl.class) {
                if (instance == null)
                    instance = new NotificationRepositoryImpl();
            }
        }
        return instance;
    }

    private NotificationRepositoryImpl() {
        super();
    }

}
