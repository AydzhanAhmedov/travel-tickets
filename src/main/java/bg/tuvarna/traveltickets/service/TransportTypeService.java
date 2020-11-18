package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.TransportType;

import java.util.List;

public interface TransportTypeService {

    List<TransportType> findAll();

    TransportType findById(Long id);

    TransportType findByName(TransportType.Enum transportTypeName);

}
