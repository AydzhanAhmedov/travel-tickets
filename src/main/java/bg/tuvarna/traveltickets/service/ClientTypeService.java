package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.ClientType;

import java.util.List;

public interface ClientTypeService {

    List<ClientType> findAll();

    ClientType findByName(ClientType.Enum clientTypeName);
}
