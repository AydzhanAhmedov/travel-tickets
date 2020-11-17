package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Company;
import bg.tuvarna.traveltickets.entity.Distributor;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.ClientService;

import java.util.Arrays;
import java.util.List;

import static bg.tuvarna.traveltickets.entity.ClientType.Enum.COMPANY;
import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    //private final ClientTypeService clientTypeService = ClientTypeServiceImpl.getInstance();
    //TODO remove comment

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

    @Override
    public Client addClient(Client client) {

        // add city
        City city = CityServiceImpl.getInstance().findOrAddByName(client.getAddress().getCity().getName());
        client.getAddress().setCity(city);

        // add user
        userRepository.save(client.getUser());
        client.setUserId(client.getUser().getId());

        // add client
        ClientRepositoryImpl.getInstance().save(client);
        return client;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> findAllCompaniesAndDistributors() {
        return clientRepository.findAllByClientTypeIds(Arrays.asList(ClientTypeServiceImpl.getInstance().findByName(DISTRIBUTOR).getId(),
                ClientTypeServiceImpl.getInstance().findByName(COMPANY).getId()));
    }

    @Override
    public List<Client> findAllCashiersForLoggedUser() {
        return (List<Client>) (List<?>) clientRepository.findAllCashiersByDistributorIds(List.of(AuthServiceImpl.getInstance().getLoggedClient().getUserId()));
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

    private ClientServiceImpl() {
        super();
    }

}
