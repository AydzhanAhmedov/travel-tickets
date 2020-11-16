package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Client;

import java.util.List;

public interface ClientService {

    Client findByUserId(Long userId);

    Client addClient(Client client);

    List<Client> findAll();
}
