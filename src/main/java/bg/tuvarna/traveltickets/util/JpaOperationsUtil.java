package bg.tuvarna.traveltickets.util;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;

import static bg.tuvarna.traveltickets.common.Constants.ACTION_CANNOT_BE_NULL_MESSAGE;
import static bg.tuvarna.traveltickets.common.Constants.CANNOT_BE_INSTANTIATED_FORMAT;

/**
 * This utility class provides a set of methods for working with JPA.
 */
public final class JpaOperationsUtil {

    private static final Logger LOG = LogManager.getLogger(JpaOperationsUtil.class);

    /**
     * This functional interface is used to encapsulate one or more database operations which
     * are going to be executed as a single function invoked in a single transaction.
     *
     * @param <T> return type of the function.
     */
    @FunctionalInterface
    public interface PersistentFunction<T> {
        T execute(EntityManager em);
    }

    public static <T> T execute(final PersistentFunction<T> action) {
        final boolean closeEntityManager = !EntityManagerUtil.entityManagerIsInstantiated();
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();

        final T actionResult = Objects.requireNonNull(action, ACTION_CANNOT_BE_NULL_MESSAGE).execute(entityManager);

        if (closeEntityManager) EntityManagerUtil.closeEntityManager();

        return actionResult;
    }

    public static <T> T executeInTransaction(final PersistentFunction<T> action) {
        Objects.requireNonNull(action, ACTION_CANNOT_BE_NULL_MESSAGE);

        final boolean closeEntityManager = !EntityManagerUtil.entityManagerIsInstantiated();

        final EntityManager entityManager = EntityManagerUtil.getEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();

        T actionResult;
        boolean commitTransaction = false;

        try {
            if (commitTransaction = !transaction.isActive()) {
                transaction.begin();
                LOG.debug("Transaction opened.");
            }

            actionResult = action.execute(entityManager);

            if (commitTransaction) {
                transaction.commit();
                LOG.debug("Transaction successfully committed.");
            }
        }
        catch (Exception e) {
            if (commitTransaction) {
                LOG.debug("Error caused transaction rollback: {}", e.getMessage());
                transaction.rollback();
                LOG.warn("Transaction rolled back.");
            }
            throw e;
        }
        finally {
            if (closeEntityManager) EntityManagerUtil.closeEntityManager();
        }

        return actionResult;
    }

    public static <T> T getSingleResultOrNull(final TypedQuery<T> query) {
        final List<T> resultList = query.getResultList();

        if (resultList.size() > 1) {
            throw new NonUniqueResultException();
        }
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public static <T> Task<T> createTask(final PersistentFunction<T> action) {
        return new Task<>() {
            @Override
            protected T call() {
                return execute(action);
            }
        };
    }

    public static <T> Task<T> createTransactionalTask(final PersistentFunction<T> action) {
        return new Task<>() {
            @Override
            protected T call() {
                return executeInTransaction(action);
            }
        };
    }

    private JpaOperationsUtil() {
        throw new UnsupportedOperationException(CANNOT_BE_INSTANTIATED_FORMAT.formatted(getClass().toString()));
    }

}
