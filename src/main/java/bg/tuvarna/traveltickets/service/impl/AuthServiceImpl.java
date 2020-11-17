package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.common.AppScreens;
import bg.tuvarna.traveltickets.common.MenuContent;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.EnumSet;
import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.CLIENT_NOT_FOUND_FORMAT;
import static bg.tuvarna.traveltickets.entity.Role.Enum.ADMIN;

public final class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private User loggedUser;
    private Client loggedClient;

    private final List<MenuContent> adminContent = List.of(
            MenuContent.BTN_CLIENTS,
            MenuContent.BTN_STATISTIC
    );

    private final List<MenuContent> companyContent = List.of(
            MenuContent.BTN_TRAVELS,
            MenuContent.BTN_REQUESTS,
            MenuContent.BTN_SOLD_TICKETS,
            MenuContent.BTN_STATISTIC
    );

    private final List<MenuContent> distributorContent = List.of(
            MenuContent.BTN_CLIENTS,
            MenuContent.BTN_TRAVELS,
            MenuContent.BTN_SOLD_TICKETS,
            MenuContent.BTN_STATISTIC
    );

    private final List<MenuContent> cashierContent = List.of(
            MenuContent.BTN_TRAVELS,
            MenuContent.BTN_SOLD_TICKETS
    );

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

    // TODO uncomment this and use for user creation:// BCrypt.hashpw(password, BCrypt.gensalt());
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
    public void logout() {
        loggedUser = null;
        loggedClient = null;
        AppScreens.HOME.delete();
    }

    @Override
    public List<MenuContent> getLoggedUserMenuContent() {
        if (loggedUserIsAdmin())
            return adminContent;

        return switch (getLoggedClientTypeName()) {
            case COMPANY -> companyContent;
            case DISTRIBUTOR -> distributorContent;
            case CASHIER -> cashierContent;
        };
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
        super();
    }

}
