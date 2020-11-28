package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.repository.UserRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;
import bg.tuvarna.traveltickets.util.JpaOperationsUtil;

import javax.persistence.TypedQuery;

import static bg.tuvarna.traveltickets.common.Constants.EMAIL_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USERNAME_OR_EMAIL_PARAM;
import static bg.tuvarna.traveltickets.common.Constants.USERNAME_PARAM;

public class UserRepositoryImpl extends GenericCrudRepositoryImpl<User, Long> implements UserRepository {

    private static final String FIND_BY_USERNAME_OR_EMAIL_HQL = """
                SELECT u FROM User u
                WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail
            """;

    private static final String FIND_BY_USERNAME_HQL = """
                SELECT u FROM User u
                WHERE u.username = :username
            """;

    private static final String FIND_BY_EMAIL_HQL = """
                SELECT u FROM User u
                WHERE u.email = :email
            """;

    @Override
    public User findByUsernameOrEmail(final String usernameOrEmail) {
        final TypedQuery<User> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_USERNAME_OR_EMAIL_HQL, User.class)
                .setParameter(USERNAME_OR_EMAIL_PARAM, usernameOrEmail);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

    @Override
    public User findByUsername(final String username) {
        final TypedQuery<User> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_USERNAME_HQL, User.class)
                .setParameter(USERNAME_PARAM, username);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

    @Override
    public User findByEmail(final String email) {
        final TypedQuery<User> query = EntityManagerUtil.getEntityManager()
                .createQuery(FIND_BY_EMAIL_HQL, User.class)
                .setParameter(EMAIL_PARAM, email);

        return JpaOperationsUtil.getSingleResultOrNull(query);
    }

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

}
