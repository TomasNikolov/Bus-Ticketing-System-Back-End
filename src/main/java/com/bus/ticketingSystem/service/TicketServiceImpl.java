package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.repository.TicketRepository;
import com.bus.ticketingSystem.service.interfaces.BusService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import com.bus.ticketingSystem.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    private TicketRepository ticketRepository;
    private BusService busService;
    private UserService userService;

    public TicketServiceImpl(TicketRepository ticketRepository, BusService busService, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.busService = busService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public List<Ticket> reserveTickets(List<TicketDTO> tickets) {
        List<Ticket> ticketsForSave = new ArrayList<>();

        int reservedSeats = tickets.get(0).getReservedTickets();
        for (TicketDTO item : tickets) {
            ticketsForSave.add(createTicket(reservedSeats, item));
            reservedSeats++;
        }

        List<Ticket> alreadySavedTickets = ticketRepository.saveAllAndFlush(ticketsForSave);

        if (alreadySavedTickets.size() == tickets.size()) {
            busService.updateBusSeats(tickets.get(0).getBusId(), reservedSeats);
        }

        return alreadySavedTickets;
    }

    @Override
    @Transactional
    public Ticket updateTicketStatus(long id, boolean isPayed) {
        Ticket ticket = unwrapTicket(ticketRepository.findById(id));
        ticket.setPayed(isPayed);
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> getTicketsByUserId(long userId) {
        return ticketRepository.findTicketsByUserId(userId);
    }

    private Ticket createTicket(int reservedSeats, TicketDTO item) {
        Ticket ticket = new Ticket();
        ticket.setBusId(item.getBusId());
        ticket.setUserId(item.getUserId());
        ticket.setPassengerName(item.getPassengerName());
        ticket.setStartDestination(item.getStartDestination());
        ticket.setEndDestination(item.getEndDestination());
        ticket.setIssueDate(LocalDateTime.now());
        ticket.setSeatNumber(generateSeatNumber(reservedSeats));
        ticket.setPayed(false);

        return ticket;
    }

    private int generateSeatNumber(int reservedSeats) {
        return reservedSeats == 0 ? 1 : reservedSeats + 1;
    }

    private static Ticket unwrapTicket(Optional<Ticket> entity) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any tickets with this ID in our system.");
    }
}
