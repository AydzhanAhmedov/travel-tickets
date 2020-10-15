package bg.tuvarna.traveltickets.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.Constants.ACTION_CANNOT_BE_NULL_MESSAGE;
import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;
import static bg.tuvarna.traveltickets.common.Constants.PERSISTENT_UNIT_NAME;

/**
 * This utility class holds an instance of {@link EntityManagerFactory} and provides methods for working with it.
 * It class also encapsulates logic for working with {@link EntityTransaction}s and {@link EntityManager}s in a
 * thread-local manner.
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

    /**
     * This functional interface is used to encapsulate one or more database operations which
     * are going to be executed as a single function invoked in a single transaction.
     *
     * @param <T> return type of the function.
     */
    @FunctionalInterface
    public interface PersistentFunction<T> {
        T execute();
    }

    public static <T> T execute(final PersistentFunction<T> action) {
        final T actionResult = Objects.requireNonNull(action, ACTION_CANNOT_BE_NULL_MESSAGE).execute();
        closeEntityManager();
        return actionResult;
    }

    public static <T> T executeInTransaction(final PersistentFunction<T> action) {
        return executeInTransaction(action, true);
    }

    public static <T> T executeInTransaction(final PersistentFunction<T> action, final boolean closeEntityManager) {
        Objects.requireNonNull(action, ACTION_CANNOT_BE_NULL_MESSAGE);

        final EntityManager entityManager = getEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();

        T actionResult;
        boolean commitTransaction = false;

        try {
            if (commitTransaction = !transaction.isActive()) transaction.begin();

            actionResult = action.execute();

            if (commitTransaction) transaction.commit();
        }
        catch (Exception e) {
            if (commitTransaction) transaction.rollback();
            throw e;
        }
        finally {
            if (closeEntityManager) closeEntityManager();
        }

        return actionResult;
    }

    private EntityManagerUtil() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
