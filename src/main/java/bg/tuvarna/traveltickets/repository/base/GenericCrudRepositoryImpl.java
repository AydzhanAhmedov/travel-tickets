package bg.tuvarna.traveltickets.repository.base;

import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

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
    public E save(final E entity) {
        return EntityManagerUtil.executeInTransaction(() -> persistOrMerge(entity), false);
    }

    @Override
    public void delete(final E entity) {
        EntityManagerUtil.executeInTransaction(() -> {
            EntityManagerUtil.getEntityManager().remove(entity);
            return null;
        }, false);
    }

    @SuppressWarnings("unchecked")
    protected final ID getEntityId(final E entity) {
        return (ID) EntityManagerUtil.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    private E persistOrMerge(final E entity) {
        final EntityManager entityManager = EntityManagerUtil.getEntityManager();

        if (getEntityId(entity) != null) {
            return entityManager.merge(entity);
        }

        entityManager.persist(entity);
        return entity;
    }

}