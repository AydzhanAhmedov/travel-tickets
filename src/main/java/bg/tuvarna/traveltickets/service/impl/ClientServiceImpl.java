package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.service.ClientService;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();

    @Override
    public Client findByUserId(final Long userId) {
        final ClientType clientType = clientRepository.findTypeByUserId(userId);

        if (clientType == null) {
            return null;
        }

        final Class<? extends Client> clientClass = switch (clientType.getName()) {
            case COMPANY -> Company.class;
            case CASHIER -> Cashier.class;
            case DISTRIBUTOR -> Distributor.class;
        };

        return clientRepository.findById(clientClass, userId);
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
