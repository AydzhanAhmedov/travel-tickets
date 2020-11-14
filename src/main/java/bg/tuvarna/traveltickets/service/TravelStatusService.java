package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.TravelStatus;

import java.util.List;

public interface TravelStatusService {

    List<TravelStatus> findAll();

    TravelStatus findByName(TravelStatus.Enum travelStatusName);

}
