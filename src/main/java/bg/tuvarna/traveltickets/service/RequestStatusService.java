package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.RequestStatus;

import java.util.List;

public interface RequestStatusService {

    List<RequestStatus> findAll();

    RequestStatus findByName(RequestStatus.Enum requestStatusName);

}
