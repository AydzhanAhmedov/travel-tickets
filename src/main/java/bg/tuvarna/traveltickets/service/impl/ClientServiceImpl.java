package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.CityService;
import bg.tuvarna.traveltickets.service.ClientService;

import java.util.Collections;
import java.util.List;

import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static java.util.Collections.singletonList;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final CityService cityService = CityServiceImpl.getInstance();

    @Override
    public Client findByUserId(final Long userId) {
        return clientRepository.findById(userId);
    }

    @Override
    public Client addClient(final Client client) {
        // add city
        final City city = cityService.findOrAddByName(client.getAddress().getCity().getName());
        client.getAddress().setCity(city);

        // add user
        userRepository.save(client.getUser());
        client.setUserId(client.getUser().getId());

        // add client
        return clientRepository.save(client);
    }

    @Override
    public List<Client> findAll() {
        if (authService.loggedUserIsAdmin()) {
            return clientRepository.findAll();
        }
        return authService.getLoggedClientTypeName() == DISTRIBUTOR
                ? clientRepository.findAllCashiersByDistributorIds(singletonList(authService.getLoggedUser().getId()))
                : Collections.emptyList();
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
