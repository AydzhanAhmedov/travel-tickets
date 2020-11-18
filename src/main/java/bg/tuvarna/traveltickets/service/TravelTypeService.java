package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.TravelType;

import java.util.List;

public interface TravelTypeService {

    List<TravelType> findAll();

    TravelType findById(Long id);

    TravelType findByName(TravelType.Enum travelTypeName);

}
