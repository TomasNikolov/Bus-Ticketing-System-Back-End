package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.repository.TicketRepository;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private TicketRepository ticketRepository;
    private BusService busService;

    public BookingServiceImpl(TicketRepository ticketRepository, BusService busService) {
        this.ticketRepository = ticketRepository;
        this.busService = busService;
    }

    @Override
    @Transactional
    public List<Ticket> reserveTickets(List<TicketDTO> tickets) {
        List<Ticket> ticketsForSave = new ArrayList<>();
        int reservedSeats = tickets.get(0).getReservedTickets();
        for (TicketDTO item : tickets) {
            Ticket ticket = new Ticket();
            ticket.setBusId(item.getBusId());
            ticket.setPassengerName(item.getPassengerName());
            ticket.setStartDestination(item.getStartDestination());
            ticket.setEndDestination(item.getEndDestination());
            ticket.setIssueDate(LocalDateTime.now());
            ticket.setSeatNumber(generateSeatNumber(reservedSeats));

            ticketsForSave.add(ticket);
            reservedSeats++;
        }

        List<Ticket> alreadySavedTickets = ticketRepository.saveAllAndFlush(ticketsForSave);

        if (alreadySavedTickets.size() == tickets.size()) {
            busService.updateBusSeats(tickets.get(0).getBusId(), reservedSeats);
        }

        return alreadySavedTickets;
    }

    private int generateSeatNumber(int reservedSeats) {
        return reservedSeats == 0 ? 1 : reservedSeats + 1;
    }
}
