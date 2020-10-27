package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

public interface ClientRepository extends GenericCrudRepository<Client, Long> {

    ClientType findTypeByUserId(Long userId);

    <T extends Client> T findById(Class<T> clientClass, Long userId);

}
