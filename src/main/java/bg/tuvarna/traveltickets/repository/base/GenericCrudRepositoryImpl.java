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

    public Optional<E> findById(final ID id) {
        return Optional.ofNullable(EntityManagerUtil.getEntityManager().find(entityClass, id));
    }

    public E save(final E entity) {
        EntityManagerUtil.getEntityManager().persist(entity);
        return entity;
    }

    public E update(final E entity) {
        return EntityManagerUtil.getEntityManager().merge(entity);
    }

    public void delete(final E entity) {
        EntityManagerUtil.getEntityManager().remove(entity);
    }

}