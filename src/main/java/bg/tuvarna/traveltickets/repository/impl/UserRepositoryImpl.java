package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import java.util.Optional;

import static bg.tuvarna.traveltickets.common.Constants.PASSWORD_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USERNAME_PARAM;

public class UserRepositoryImpl extends GenericCrudRepositoryImpl<User, Long> implements UserRepository {

    private static UserRepositoryImpl instance;

    public static UserRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) instance = new UserRepositoryImpl();
            }
        }
        return instance;
    }

    private UserRepositoryImpl() {
        super();
    }

    private static final String FIND_BY_USERNAME_AND_PASSWORD_JPQL = """
                SELECT u FROM User u
                LEFT JOIN FETCH u.role
                WHERE u.username = :username AND u.password = :password
            """;

    @Override
    public Optional<User> findByUsernameAndPassword(final String username, final String password) {
        final User found = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_USERNAME_AND_PASSWORD_JPQL, User.class)
                .setParameter(USERNAME_PARAM, username)
                .setParameter(PASSWORD_PARAM, password)
                .getSingleResult();

        return Optional.ofNullable(found);
    }

}
