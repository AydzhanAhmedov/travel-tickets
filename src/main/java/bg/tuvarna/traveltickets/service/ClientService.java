package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Client;

public interface ClientService {

    Client findByUserId(Long userId);

}
