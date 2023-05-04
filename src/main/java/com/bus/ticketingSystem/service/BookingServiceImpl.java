package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.BookingDTO;
import com.bus.ticketingSystem.entity.Booking;
import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.repository.BookingRepository;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.BusService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class BookingServiceImpl implements BookingService {
    private TicketService ticketService;
    private BusService busService;
    private BookingRepository bookingRepository;

    public BookingServiceImpl(TicketService ticketService, BusService busService, BookingRepository bookingRepository) {
        this.ticketService = ticketService;
        this.busService = busService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public List<Booking> createBookings(long userId) {
        List<Booking> bookings = new ArrayList<>();
        List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
        Map<Long, Bus> busMap = generateBusMap(tickets);

        for (Ticket ticket : tickets) {
            Bus bus = busMap.get(ticket.getId());
            bookings.add(createBooking(ticket, bus));
        }

        return bookingRepository.saveAllAndFlush(bookings);
    }

    @Override
    public List<BookingDTO> getBookingsByUserId(long userId) {
        List<BookingDTO> bookingsData = new ArrayList<>();

        List<Booking> bookings = bookingRepository.findBookingByUserId(userId);
        for (Booking booking : bookings) {
            bookingsData.add(createBookingDTO(booking));
        }

        return bookingsData;
    }

    private static BookingDTO createBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setStartDestination(booking.getStartDestination());
        bookingDTO.setEndDestination(booking.getEndDestination());
        bookingDTO.setDepartureDate(booking.getDepartureDate());
        bookingDTO.setDepartureTime(booking.getDepartureTime());
        bookingDTO.setArrivalDate(booking.getArrivalDate());
        bookingDTO.setArrivalTime(booking.getArrivalTime());
        bookingDTO.setBusName(booking.getBusName());
        bookingDTO.setPrice(booking.getPrice());

        if (booking.isActive()) {
            bookingDTO.setStatus("Active");
        } else {
            bookingDTO.setStatus("Canceled");
        }

        return bookingDTO;
    }

    private static Booking createBooking(Ticket ticket, Bus bus) {
        Booking booking = new Booking();
        booking.setStartDestination(bus.getStartDestination());
        booking.setEndDestination(bus.getEndDestination());
        booking.setDepartureDate(bus.getDepartureDate());
        booking.setDepartureTime(bus.getDepartureTime());
        booking.setArrivalDate(bus.getArrivalDate());
        booking.setArrivalTime(bus.getArrivalTime());
        booking.setBusName(bus.getName());
        booking.setBusId(bus.getId());
        booking.setTicketId(ticket.getId());
        booking.setPrice(ticket.getPrice());
        booking.setUserId(ticket.getUserId());
        booking.setActive(true);

        return booking;
    }

    private Map<Long, Bus> generateBusMap(List<Ticket> tickets) {
        Map<Long, Bus> result = new HashMap<>();
        List<Bus> buses = busService.getBussesByIds(getBusIds(tickets));

        for (Ticket ticket : tickets) {
            result.put(ticket.getId(), busService.getBusById(ticket.getBusId(), buses));
        }

        return result;
    }

    private Set<Long> getBusIds(List<Ticket> tickets) {
        Set<Long> result = new HashSet<>();
        for (Ticket ticket : tickets) {
            result.add(ticket.getBusId());
        }

        return result;
    }
}
