package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;

public class ClientRepositoryImpl extends GenericCrudRepositoryImpl<Client, Long> implements ClientRepository {

    private static ClientRepositoryImpl instance;

    public static ClientRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (ClientRepositoryImpl.class) {
                if (instance == null)
                    instance = new ClientRepositoryImpl();
            }
        }
        return instance;
    }

    public ClientRepositoryImpl() {
        super();
    }
}
