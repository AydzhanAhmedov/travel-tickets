package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;

public interface TravelService {

    Travel create(Travel travel);

    Travel updateTravelStatus(Travel travel, TravelStatus.Enum newStatusName);

}
