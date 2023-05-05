package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> reserveTickets(List<TicketDTO> tickets);
    List<Ticket> payTickets(long userId);
    List<Ticket> getUnpaidTicketsByUserId(long userId);
    void deleteTicket(long id);
}
