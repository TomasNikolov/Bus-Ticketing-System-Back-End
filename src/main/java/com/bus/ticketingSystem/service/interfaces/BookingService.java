package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;

import java.util.List;

public interface BookingService {
    List<Ticket> reserveTickets(List<TicketDTO> tickets);
}
