package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.BookingDTO;
import com.bus.ticketingSystem.entity.Booking;
import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
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
        List<Ticket> tickets = ticketService.getUnpaidTicketsByUserId(userId);
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

    @Override
    @Transactional
    public void cancelBooking(long id) {
        Booking booking = unwrapBooking(bookingRepository.findById(id), id);
        ticketService.deleteTicket(booking.getTicket().getId());
        busService.updateBusSeatsAfterBookingCancellation(booking.getBus().getId());
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        //TODO: Change this method to return only the bookings for the current admin company user
        return bookingRepository.findAll();
    }

    @Override
    @Transactional
    public Booking updateBooking(BookingDTO bookingDTO) {
        Booking booking = unwrapBooking(bookingRepository.findById(bookingDTO.getId()), bookingDTO.getId());
        booking.setStartDestination(bookingDTO.getStartDestination());
        booking.setEndDestination(bookingDTO.getEndDestination());
        booking.setDepartureTime(bookingDTO.getDepartureTime());
        booking.setDepartureDate(bookingDTO.getDepartureDate());
        booking.setArrivalTime(bookingDTO.getArrivalTime());
        booking.setArrivalDate(bookingDTO.getArrivalDate());
        booking.setPrice(bookingDTO.getPrice());
        booking.setActive(bookingDTO.getStatus().equals("Active"));

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void deleteBookings(long userId) {
        bookingRepository.deleteAllInBatch(bookingRepository.findBookingByUserId(userId));
    }

    @Override
    public void deleteBookingsByBusId(long busId) {
        bookingRepository.deleteAllInBatch(bookingRepository.findBookingByBusId(busId));
    }

    private static BookingDTO createBookingDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setTicketId(booking.getTicket().getId());
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
        booking.setBus(bus);
        booking.setTicket(ticket);
        booking.setPrice(ticket.getPrice());
        booking.setUser(ticket.getUser());
        booking.setActive(true);

        return booking;
    }

    private Map<Long, Bus> generateBusMap(List<Ticket> tickets) {
        Map<Long, Bus> result = new HashMap<>();
        List<Bus> buses = busService.getBussesByIds(getBusIds(tickets));

        for (Ticket ticket : tickets) {
            result.put(ticket.getId(), busService.getBusById(ticket.getBus().getId(), buses));
        }

        return result;
    }

    private Set<Long> getBusIds(List<Ticket> tickets) {
        Set<Long> result = new HashSet<>();
        for (Ticket ticket : tickets) {
            result.add(ticket.getBus().getId());
        }

        return result;
    }

    private static Booking unwrapBooking(Optional<Booking> entity, long id) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any bookings with this ID: " + id + " in our system.");
    }
}
