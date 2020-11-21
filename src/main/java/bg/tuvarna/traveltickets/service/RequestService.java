package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.TravelDistributorRequest;

import java.util.List;

public interface RequestService {

    List<TravelDistributorRequest> findAll();
}
