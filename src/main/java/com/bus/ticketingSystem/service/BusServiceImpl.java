package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.DTO.Reservation;
import com.bus.ticketingSystem.entity.User;
import com.bus.ticketingSystem.exception.EntityNotFoundException;
import com.bus.ticketingSystem.repository.BusRepository;
import com.bus.ticketingSystem.service.interfaces.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        bus.setReservedSeats(reservedTickets);
        bus.setAvailableSeats(bus.getCapacity() - reservedTickets);
        return busRepository.save(bus);
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
