package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.NotificationType;

import java.util.List;

public interface NotificationTypeService {

    List<NotificationType> findAll();

    NotificationType findById(Long id);

    NotificationType findByName(NotificationType.Enum notificationTypeName);

}
