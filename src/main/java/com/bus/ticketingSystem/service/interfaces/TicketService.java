package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> reserveTickets(List<TicketDTO> tickets);
    Ticket updateTicketStatus(long id, boolean isPayed);
    List<Ticket> getTicketsByUserId(long userId);
}
