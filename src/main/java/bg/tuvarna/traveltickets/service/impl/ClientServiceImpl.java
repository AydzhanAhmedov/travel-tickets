package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.service.ClientService;

import java.util.Optional;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();

    @Override
    public ClientType.Enum getClientType() {
        User user = UserServiceImpl.getInstance().getCurrentlyLoggedUser();
        Optional<Client> client = clientRepository.findById(user.getId());
        return client.get().getClientType().getName();
    }

    private static ClientServiceImpl instance;

    public static ClientServiceImpl getInstance() {
        if (instance == null) {
            synchronized (ClientServiceImpl.class) {
                if (instance == null)
                    instance = new ClientServiceImpl();
            }
        }
        return instance;
    }


    public ClientServiceImpl() {
        super();
    }

}
