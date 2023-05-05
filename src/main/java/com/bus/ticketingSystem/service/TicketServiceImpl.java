package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.repository.TicketRepository;
import com.bus.ticketingSystem.service.interfaces.BusService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import com.bus.ticketingSystem.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
        Map<Integer, Boolean> busSeats = generateSeatsMap(tickets.get(0));

//        int reservedSeats = tickets.get(0).getReservedTickets();
        for (TicketDTO item : tickets) {
            ticketsForSave.add(createTicket(item));
//            reservedSeats++;
        }

        List<Ticket> alreadySavedTickets = ticketRepository.saveAllAndFlush(generateSeatNumbers(ticketsForSave, busSeats));

        if (alreadySavedTickets.size() == tickets.size()) {
            busService.updateBusSeats(tickets.get(0).getBusId(), tickets.size());
        }

        return alreadySavedTickets;
    }

    @Override
    @Transactional
    public List<Ticket> payTickets(long userId) {
        List<Ticket> tickets = ticketRepository.findUnpaidTicketsByUserId(userId);
        for (Ticket ticket : tickets) {
            ticket.setPayed(true);
        }
        return ticketRepository.saveAllAndFlush(tickets);
    }

    @Override
    public List<Ticket> getUnpaidTicketsByUserId(long userId) {
        return ticketRepository.findUnpaidTicketsByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteTicket(long id) {
        ticketRepository.deleteById(id);
    }

    private Ticket createTicket(TicketDTO item) {
        Ticket ticket = new Ticket();
        ticket.setBusId(item.getBusId());
        ticket.setUserId(item.getUserId());
        ticket.setPassengerName(item.getPassengerName());
        ticket.setStartDestination(item.getStartDestination());
        ticket.setEndDestination(item.getEndDestination());
        ticket.setIssueDate(LocalDateTime.now());
//        ticket.setSeatNumber(generateSeatNumber(reservedSeats));
        ticket.setPayed(false);

        return ticket;
    }

    private Map<Integer, Boolean> generateSeatsMap(TicketDTO ticket) {
        Map<Integer, Boolean> result = new HashMap<>();
        for (int i = 1; i <= ticket.getBusCapacity(); i++) {
            result.put(i, false);
        }

        return pushReservedSeats(result, ticket.getBusId());
    }

    private Map<Integer, Boolean> pushReservedSeats(Map<Integer, Boolean> result, long busId) {
        List<Ticket> reservedTickets = ticketRepository.findTicketsByBusId(busId);
        for (Ticket ticket : reservedTickets) {
            result.put(ticket.getSeatNumber(), true);
        }

        System.out.println("SEATS MAP: " + result);
        return result;
    }

    private List<Ticket> generateSeatNumbers(List<Ticket> tickets, Map<Integer, Boolean> reservedSeats) {
        for (Ticket ticket : tickets) {
            for (Map.Entry<Integer, Boolean> entry : reservedSeats.entrySet()) {
                if (!entry.getValue()) {
                    entry.setValue(true);
                    ticket.setSeatNumber(entry.getKey());
                    System.out.println("RESERVING SEAT WITH NUMBER " + entry.getKey() + " FOR PASSENGER " + ticket.getPassengerName());
                    break;
                }
            }
        }

        return tickets;
    }

    private static Ticket unwrapTicket(Optional<Ticket> entity) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any tickets with this ID in our system.");
    }
}
