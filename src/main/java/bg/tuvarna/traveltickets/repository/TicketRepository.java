package bg.tuvarna.traveltickets.repository;

import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepository;

import java.util.List;

public interface TicketRepository extends GenericCrudRepository<Ticket, Long> {

    List<Ticket> findAllByCashierId(final long cashierId);

    List<Ticket> findAllByDistributorId(final long distributorId);

    List<Ticket> findAllByCompanyId(final long companyId);
}
