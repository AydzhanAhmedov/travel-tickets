package bg.tuvarna.traveltickets.service.impl;

import bg.tuvarna.traveltickets.entity.Ticket;
import bg.tuvarna.traveltickets.entity.Travel;
import bg.tuvarna.traveltickets.repository.TicketRepository;
import bg.tuvarna.traveltickets.repository.TravelRepository;
import bg.tuvarna.traveltickets.repository.impl.TicketRepositoryImpl;
import bg.tuvarna.traveltickets.repository.impl.TravelRepositoryImpl;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.TicketService;

import java.util.List;

public class TicketServiceImpl implements TicketService {

    private static TicketServiceImpl instance;

    private final AuthService authService = AuthServiceImpl.getInstance();
    private final TicketRepository ticketRepository = TicketRepositoryImpl.getInstance();
    private final TravelRepository travelRepository = TravelRepositoryImpl.getInstance();

    public static TicketServiceImpl getInstance() {
        if (instance == null) {
            synchronized (TicketServiceImpl.class) {
                if (instance == null)
                    instance = new TicketServiceImpl();
            }
        }
        return instance;
    }

    private TicketServiceImpl() {
        super();
    }

    @Override
    public Ticket save(final Ticket ticket) {

        Travel travel = ticket.getTravel();
        travel.setCurrentTicketQuantity(travel.getCurrentTicketQuantity()-1);
        travelRepository.save(travel);
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> findAll() {

        Long clientId = authService.getLoggedUser().getId();

        return switch (authService.getLoggedClientTypeName()) {
            case CASHIER -> ticketRepository.findAllByCashierId(clientId);
            case COMPANY -> ticketRepository.findAllByCompanyId(clientId);
            case DISTRIBUTOR -> ticketRepository.findAllByDistributorId(clientId);
            default -> throw new IllegalStateException("Unexpected value: " + authService.getLoggedClientTypeName());
        };
    }
}
