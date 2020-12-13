package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.Role;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static bg.tuvarna.traveltickets.common.Constants.CLIENT_NOT_FOUND_FORMAT;
import static bg.tuvarna.traveltickets.entity.Role.Enum.ADMIN;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

public final class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LogManager.getLogger(AuthServiceImpl.class);

    private final Map<Long, Role> rolesByIdCache;
    private final Map<Role.Enum, Role> rolesByNameCache;

    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private User loggedUser;
    private Client loggedClient;

    @Override
    public User getLoggedUser() {
        return loggedUser;
    }

    @Override
    public Client getLoggedClient() {
        return loggedClient;
    }

    @Override
    public ClientType.Enum getLoggedClientTypeName() {
        return loggedClient != null ? loggedClient.getClientType().getName() : null;
    }

    @Override
    public boolean loggedUserIsAdmin() {
        return loggedUser != null && ADMIN.equals(loggedUser.getRole().getName());
    }

    @Override
    public User login(final String usernameOrEmail, final String password) {
        final User user = userRepository.findByUsernameOrEmail(usernameOrEmail);

        loggedUser = user != null && BCrypt.checkpw(password, user.getPassword()) ? user : null;
        loggedClient = loggedUser != null && !loggedUserIsAdmin() ? ClientServiceImpl.getInstance().findByUserId(loggedUser.getId()) : null;

        if (loggedUser != null && !loggedUserIsAdmin() && loggedClient == null) {
            throw new RuntimeException(CLIENT_NOT_FOUND_FORMAT.formatted(loggedUser.getId()));
        }

        return loggedUser;
    }

    @Override
    public User findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void logout() {
        loggedUser = null;
        loggedClient = null;
    }

    @Override
    public Role findRoleById(final Long id) {
        return rolesByIdCache.get(Objects.requireNonNull(id));
    }

    @Override
    public Role findRoleByName(final Role.Enum roleName) {
        return rolesByNameCache.get(Objects.requireNonNull(roleName));
    }

    private static AuthServiceImpl instance;

    public static AuthServiceImpl getInstance() {
        if (instance == null) {
            synchronized (AuthServiceImpl.class) {
                if (instance == null) instance = new AuthServiceImpl();
            }
        }
        return instance;
    }

    private AuthServiceImpl() {
        final List<Role> roles = JpaOperationsUtil.execute(em ->
                em.createQuery("FROM Role", Role.class)
                        .getResultStream()
                        .collect(toUnmodifiableList())
        );

        rolesByIdCache = roles.stream()
                .collect(toUnmodifiableMap(Role::getId, Function.identity()));

        rolesByNameCache = roles.stream()
                .collect(toUnmodifiableMap(Role::getName, Function.identity()));

        LOG.info("{} instantiated, roles fetched and cached.", getClass());
    }

}
