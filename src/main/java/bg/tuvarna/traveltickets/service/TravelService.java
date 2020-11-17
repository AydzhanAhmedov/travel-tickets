package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;

import java.util.List;

public interface TravelService {

    List<Travel> findAll();

    Travel create(Travel travel);

    Travel updateTravelStatus(Travel travel, TravelStatus.Enum newStatusName);

}
