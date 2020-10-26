package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.impl.UserRepositoryImpl;
import bg.tuvarna.traveltickets.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = UserRepositoryImpl.getInstance();

    private User currentlyLoggedUser;

    @Override
    public boolean login(final String usernameOrEmail, final String password) {
        final Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .filter(u -> u.getPassword().equals(password));

        user.ifPresent(u -> currentlyLoggedUser = u);

        return user.isPresent();
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

    public User getCurrentlyLoggedUser() {
        return currentlyLoggedUser;
    }
}
