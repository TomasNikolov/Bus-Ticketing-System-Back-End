package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;

import java.util.List;
import java.util.Set;

public interface TicketService {
    List<Ticket> reserveTickets(List<TicketDTO> tickets);

    List<Ticket> payTickets(long userId);

    List<Ticket> getUnpaidTicketsByUserId(long userId);

    void deleteTicket(long id);

    void deleteTickets(Set<Long> ids);

    void sendTicket(long id);

    void deleteTicketsByUserId(long userId);

    void deleteTicketsByBusId(long busId);
}
