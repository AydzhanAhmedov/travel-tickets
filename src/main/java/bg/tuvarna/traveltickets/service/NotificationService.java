package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Notification;
import bg.tuvarna.traveltickets.entity.NotificationType;
import bg.tuvarna.traveltickets.entity.User;

import java.util.List;

public interface NotificationService {

    Notification create(String message, NotificationType.Enum notificationTypeName, List<User> recipients);

}
