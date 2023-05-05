package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.DTO.Reservation;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.repository.BusRepository;
import com.bus.ticketingSystem.service.interfaces.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BusServiceImpl implements BusService {
    private static final String BUS_NOT_FOUND_ERROR_MESSAGE = "We apologize, but we were unable to find any available buses for your selected date and destination in our system.";
    private BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public List<Bus> getBusesByDestinationAndDate(Reservation reservation) {
        List<Optional<Bus>> buses = new ArrayList<>();
        try {
            buses = busRepository.findBusByRouteAndDate(reservation.getStartDestination(), reservation.getEndDestination(), reservation.getDate());
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            System.out.println("MESSAGE: " + e.getMessage());
            System.out.println("STACK TRACE: " + Arrays.toString(e.getStackTrace()));
        }

        return unwrapBuses(buses);
    }

    @Override
    @Transactional
    public Bus updateBusSeats(Long id, int reservedTickets) {
        Bus bus = unwrapBus(busRepository.findById(id));
        System.out.println("RESERVED TICKETS: " + reservedTickets);
        bus.setReservedSeats(bus.getReservedSeats() + reservedTickets);
        System.out.println("RESERVED SEATS: " + bus.getReservedSeats());
        bus.setAvailableSeats(bus.getCapacity() - bus.getReservedSeats());
        return busRepository.save(bus);
    }

    @Override
    public List<Bus> getBussesByIds(Set<Long> ids) {
        return busRepository.findAllById(ids);
    }

    @Override
    public Bus getBusById(long id, List<Bus> buses) {
        for (Bus bus : buses) {
            if (bus.getId() == id) {
                return bus;
            }
        }

        return null;
    }

    @Override
    @Transactional
    public void updateBusSeatsAfterBookingCancellation(Long id) {
        Bus bus = unwrapBus(busRepository.findById(id));
        if (bus.getCapacity() > bus.getAvailableSeats()) {
            bus.setReservedSeats(bus.getReservedSeats() - 1);
            bus.setAvailableSeats(bus.getAvailableSeats() + 1);
        }
        busRepository.save(bus);
    }

    private static List<Bus> unwrapBuses(List<Optional<Bus>> entity) {
        List<Bus> result = new ArrayList<>();
        for (Optional<Bus> optionalBus : entity) {
            if (optionalBus.isPresent()) {
                result.add(optionalBus.get());
            }
        }

        if (result.isEmpty()) {
            throw new EntityNotFoundException(BUS_NOT_FOUND_ERROR_MESSAGE);
        }

        return result;
    }

    private static Bus unwrapBus(Optional<Bus> entity) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any buses with this ID in our system.");
    }
}
