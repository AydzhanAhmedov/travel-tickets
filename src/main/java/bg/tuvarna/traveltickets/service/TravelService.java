package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.TransportType;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.entity.TravelStatus;
import bg.tuvarna.traveltickets.entity.TravelType;

import java.util.List;

public interface TravelService {

    List<Travel> findAll();

    Travel create(Travel travel);

    Travel updateTravel(Long id, TravelStatus.Enum newStatusName, String newDetails, Integer newTicketQuantity);

    TransportType findTransportTypeById(Long id);

    TransportType findTransportTypeByName(TransportType.Enum transportTypeName);

    TravelStatus findStatusById(Long id);

    TravelStatus findStatusByName(TravelStatus.Enum travelStatusName);

    TravelType findTypeById(Long id);

    TravelType findTypeByName(TravelType.Enum travelTypeName);

}
