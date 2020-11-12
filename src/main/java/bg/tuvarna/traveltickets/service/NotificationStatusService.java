package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.NotificationStatus;

import java.util.List;

public interface NotificationStatusService {

    List<NotificationStatus> findAll();

    NotificationStatus findByName(NotificationStatus.Enum notificationStatusName);

}
