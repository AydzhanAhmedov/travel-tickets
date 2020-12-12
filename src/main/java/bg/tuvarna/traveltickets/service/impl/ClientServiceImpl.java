package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.City;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.ClientRepository;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.ClientRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.CityService;
import bg.tuvarna.traveltickets.service.ClientService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static bg.tuvarna.traveltickets.entity.ClientType.Enum.DISTRIBUTOR;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LogManager.getLogger(ClientServiceImpl.class);

    private final Map<Long, ClientType> clientTypeByIdCache;
    private final Map<ClientType.Enum, ClientType> clientTypeByNameCache;

    private final ClientRepository clientRepository = ClientRepositoryImpl.getInstance();
    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final CityService cityService = CityServiceImpl.getInstance();

    @Override
    public Client findByUserId(final Long userId) {
        return clientRepository.findById(userId);
    }

    @Override
    public Client save(final Client client) {


        if (client.getUser().getId() != null) {
            Client clientDB = clientRepository.findById(client.getUserId());
            if (!clientDB.getUser().getPassword().equals(client.getUser().getPassword())) {
                String passHash = BCrypt.hashpw(client.getUser().getPassword(), BCrypt.gensalt());
                client.getUser().setPassword(passHash);
            }
        } else {
            String passHash = BCrypt.hashpw(client.getUser().getPassword(), BCrypt.gensalt());
            client.getUser().setPassword(passHash);
        }

        final City city = cityService.findOrAddByName(client.getAddress().getCity().getName());
        client.getAddress().setCity(city);
        userRepository.save(client.getUser());
        client.setUserId(client.getUser().getId());

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

    @Override
    public ClientType findTypeById(final Long id) {
        return clientTypeByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public ClientType findTypeByName(final ClientType.Enum clientTypeName) {
        return clientTypeByNameCache.get(Objects.requireNonNull(clientTypeName));
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
        final List<ClientType> clientTypes = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM ClientType", ClientType.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        clientTypeByIdCache = clientTypes.stream()
                .collect(toUnmodifiableMap(ClientType::getId, Function.identity()));

        clientTypeByNameCache = clientTypes.stream()
                .collect(toUnmodifiableMap(ClientType::getName, Function.identity()));

        LOG.info("{} instantiated, client types fetched and cached.", getClass());
    }

}
