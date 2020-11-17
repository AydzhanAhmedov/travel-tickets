package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Cashier;
import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.ClientType;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.List;

public interface ClientRepository extends GenericCrudRepository<Client, Long> {

    ClientType findTypeByUserId(Long userId);

    <T extends Client> T findById(Class<T> clientClass, Long userId);

    List<Client> findAll();

    List<Client> findAllByClientTypeId(Long clientTypeIds);

    List<Client> findAllByClientTypeIds(List<Long> clientTypeIds);

    List<Cashier> findAllCashiersByDistributorIds(final List<Long> distributorId);

}
