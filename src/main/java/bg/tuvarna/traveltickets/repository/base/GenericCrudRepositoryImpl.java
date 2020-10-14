package bg.tuvarna.traveltickets.repository.base;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link GenericCrudRepository}.
 *
 * @param <E>  the entity type.
 * @param <ID> the entity id type.
 */
public abstract class GenericCrudRepositoryImpl<E, ID> implements GenericCrudRepository<E, ID> {

    private final Class<E> entityClass;

    /**
     * This default constructor uses Java reflection to get the {@link Class} object of
     * the entity ({@code E}). This class object is then used with {@link EntityManager#find(Class, Object)}.
     */
    @SuppressWarnings("unchecked")
    protected GenericCrudRepositoryImpl() {
        final ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<E>) pt.getActualTypeArguments()[0];
    }

    @Override
    public Optional<E> findById(final ID id) {
        return Optional.ofNullable(EntityManagerUtil.getEntityManager().find(entityClass, id));
    }

    @Override
    public E save(E entity) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();

        final Function<E, E> function = getEntityId(entity) != null ? entityManager::merge : e -> {
            entityManager.persist(e);
            return e;
        };

        if (transaction.isActive()) entity = function.apply(entity);
        else try {
            transaction.begin();
            entity = function.apply(entity);
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
            throw e;
        }

        return entity;
    }

    @Override
    public void delete(final E entity) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();

        if (transaction.isActive()) entityManager.remove(entity);
        else try {
            transaction.begin();
            entityManager.remove(entity);
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    protected final ID getEntityId(final E entity) {
        return (ID) EntityManagerUtil.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

}