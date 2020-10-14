package bg.tuvarna.traveltickets.repository.base;

import java.util.Optional;

/**
 * This interface provides generified CRUD methods to reduce repetitive code.
 *
 * @param <E>  the entity type.
 * @param <ID> the entity id type.
 */
public interface GenericCrudRepository<E, ID> {

    Optional<E> findById(ID id);

    E save(E entity);

    E update(E entity);

    void delete(E entity);

}
