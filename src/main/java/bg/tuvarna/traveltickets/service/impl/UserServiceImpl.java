package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import static bg.tuvarna.traveltickets.entity.Role.Enum.ADMIN;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private User loggedUser;

    @Override
    public User getLoggedUser() {
        return loggedUser;
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

        if (!loggedUserIsAdmin()) {

        }

        return loggedUser;
    }

    @Override
    public void logout() {
        loggedUser = null;
    }

    private static UserServiceImpl instance;

    public static UserServiceImpl getInstance() {
        if (instance == null) {
            synchronized (UserServiceImpl.class) {
                if (instance == null) instance = new UserServiceImpl();
            }
        }
        return instance;
    }

    private UserServiceImpl() {
        super();
    }

}
