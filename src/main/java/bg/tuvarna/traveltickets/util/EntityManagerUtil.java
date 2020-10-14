package bg.tuvarna.traveltickets.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.PERSISTENT_UNIT_NAME;

/**
 * Holds an instance of {@link EntityManagerFactory} and provides methods for working with it.
 * This class also encapsulates logic for creating and closing {@link EntityManager}s in a thread-local manner.
 */
public final class EntityManagerUtil {

    private static final EntityManagerFactory EMF_INSTANCE = Persistence.createEntityManagerFactory(PERSISTENT_UNIT_NAME);
    private static final ThreadLocal<EntityManager> EM_THREAD_LOCAL = new ThreadLocal<>();

    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF_INSTANCE;
    }

    public static void closeEntityManagerFactory() {
        EMF_INSTANCE.close();
    }

    public static EntityManager newEntityManager() {
        return EMF_INSTANCE.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        EntityManager entityManager = EM_THREAD_LOCAL.get();

        if (entityManager == null) {
            entityManager = newEntityManager();
            EM_THREAD_LOCAL.set(entityManager);
        }

        return entityManager;
    }

    public static void closeEntityManager() {
        final EntityManager entityManager = EM_THREAD_LOCAL.get();

        if (entityManager != null) {
            entityManager.close();
            EM_THREAD_LOCAL.set(null);
        }
    }

    private EntityManagerUtil() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}