package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import java.util.Optional;

import static bg.tuvarna.traveltickets.common.Constants.USERNAME_OR_EMAIL_PARAM;

public class UserRepositoryImpl extends GenericCrudRepositoryImpl<User, Long> implements UserRepository {

    private static UserRepositoryImpl instance;

    public static UserRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (UserRepositoryImpl.class) {
                if (instance == null) instance = new UserRepositoryImpl();
            }
        }
        return instance;
    }

    private UserRepositoryImpl() {
        super();
    }

    private static final String FIND_BY_USERNAME_AND_PASSWORD_HQL = """
                SELECT u FROM User u
                LEFT JOIN FETCH u.role
                WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail
            """;

    @Override
    public Optional<User> findByUsernameOrEmail(final String usernameOrEmail) {
        final User found = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_USERNAME_AND_PASSWORD_HQL, User.class)
                .setParameter(USERNAME_OR_EMAIL_PARAM, usernameOrEmail)
                .getSingleResult();

        return Optional.ofNullable(found);
    }

}
