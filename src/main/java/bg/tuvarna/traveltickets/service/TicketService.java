package bg.tuvarna.traveltickets.service;

import bg.tuvarna.traveltickets.entity.Ticket;

import java.util.List;

public interface TicketService {

    Ticket save(Ticket ticket);

    List<Ticket> findAll();
}
