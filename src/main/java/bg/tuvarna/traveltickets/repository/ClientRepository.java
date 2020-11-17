package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.List;

public interface ClientRepository extends GenericCrudRepository<Client, Long> {

    ClientType findTypeByUserId(Long userId);

    <T extends Client> T findById(Class<T> clientClass, Long userId);

    List<Client> findAll();

    <T extends Client> List<Client> findAllByClientTypeId(Long clientTypeId, Class<T> clientClass);

    List<Client> findAllCashiersByDistributorIds(final List<Long> distributorId);

}
