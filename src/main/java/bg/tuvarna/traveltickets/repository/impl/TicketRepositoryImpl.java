package bg.tuvarna.traveltickets.repository.impl;

import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.repository.TicketRepository;
import bg.tuvarna.traveltickets.repository.base.GenericCrudRepositoryImpl;
import bg.tuvarna.traveltickets.util.EntityManagerUtil;

import java.util.List;

import static bg.tuvarna.traveltickets.common.Constants.USER_ID_PARAM;

public class TicketRepositoryImpl extends GenericCrudRepositoryImpl<Ticket, Long> implements TicketRepository {

    private static final String FIND_ALL_BY_CASHIER_ID = """
                SELECT t FROM Ticket as t
                LEFT JOIN FETCH t.createdBy as c
                WHERE c.userId = :userId 
            """;

    private static final String FIND_ALL_BY_DISTRIBUTOR_ID = """
                SELECT t FROM Ticket as t
                LEFT JOIN FETCH t.createdBy as c
                LEFT JOIN FETCH c.createdBy as d
                WHERE d.userId = :userId
            """;

    private static final String FIND_ALL_BY_COMPANY_ID = """
                SELECT t FROM Ticket as t
                LEFT JOIN FETCH t.travel as tr
                LEFT JOIN FETCH tr.createdBy as c
                WHERE c.userId = :userId
            """;
    private static TicketRepositoryImpl instance;

    public static TicketRepositoryImpl getInstance() {
        if (instance == null) {
            synchronized (TicketRepositoryImpl.class) {
                if (instance == null) instance = new TicketRepositoryImpl();
            }
        }
        return instance;
    }

    private TicketRepositoryImpl() {
        super();
    }

    @Override
    public List<Ticket> findAllByCashierId(final long cashierId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_CASHIER_ID, Ticket.class)
                .setParameter(USER_ID_PARAM, cashierId)
                .getResultList();
    }

    @Override
    public List<Ticket> findAllByDistributorId(final long distributorId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_DISTRIBUTOR_ID, Ticket.class)
                .setParameter(USER_ID_PARAM, distributorId)
                .getResultList();
    }

    @Override
    public List<Ticket> findAllByCompanyId(final long companyId) {
        return EntityManagerUtil.getEntityManager()
                .createQuery(FIND_ALL_BY_COMPANY_ID, Ticket.class)
                .setParameter(USER_ID_PARAM, companyId)
                .getResultList();
    }
}
