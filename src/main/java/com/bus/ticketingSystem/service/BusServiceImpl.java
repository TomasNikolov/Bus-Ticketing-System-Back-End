package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.BusDTO;
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
    private static final String BUS_NOT_FOUND_ERROR_MESSAGE = "We’re sorry, but we couldn’t find any available buses for your chosen date and destination in our system. Please try adjusting your search criteria or check back later";
    private static final String BUS_NOT_FOUND_WITH_TICKET_PRICE_ERROR_MESSAGE = "We’re sorry, but we couldn’t find any available buses for your chosen date, destination and price target in our system. Please try adjusting your search criteria or check back later";
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

        return unwrapBuses(buses, false);
    }

    @Override
    public List<Bus> getBusesByDestinationDateAndTicketPrice(Reservation reservation) {
        List<Optional<Bus>> buses = new ArrayList<>();
        try {
            buses = busRepository.findBusByRouteDateAndTicketPrice(reservation.getStartDestination(),
                    reservation.getEndDestination(), reservation.getDate(), reservation.getMaxTicketPrice());
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            System.out.println("MESSAGE: " + e.getMessage());
            System.out.println("STACK TRACE: " + Arrays.toString(e.getStackTrace()));
        }

        return unwrapBuses(buses, true);
    }

    @Override
    @Transactional
    public Bus updateBusSeats(Long id, int reservedTickets) {
        Bus bus = unwrapBus(busRepository.findById(id), id);
        bus.setReservedSeats(bus.getReservedSeats() + reservedTickets);
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
        Bus bus = unwrapBus(busRepository.findById(id), id);
        if (bus.getCapacity() > bus.getAvailableSeats()) {
            bus.setReservedSeats(bus.getReservedSeats() - 1);
            bus.setAvailableSeats(bus.getAvailableSeats() + 1);
        }
        busRepository.save(bus);
    }

    @Override
    public Bus getBusById(long id) {
        return unwrapBus(busRepository.findById(id), id);
    }

    @Override
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @Override
    @Transactional
    public Bus createBus(BusDTO busDTO) {
        return busRepository.save(buildBus(busDTO));
    }

    @Override
    @Transactional
    public void createBuses(List<BusDTO> busDTOs) {
        busRepository.saveAll(buildBuses(busDTOs));
    }

    @Override
    @Transactional
    public Bus updateBus(BusDTO busDTO) {
        Bus bus = unwrapBus(busRepository.findById(busDTO.getId()), busDTO.getId());
        bus.setName(busDTO.getName());
        bus.setStartDestination(busDTO.getStartDestination());
        bus.setEndDestination(busDTO.getEndDestination());
        bus.setAvailableSeats(busDTO.getAvailableSeats());
        bus.setCapacity(busDTO.getCapacity());
        bus.setReservedSeats(busDTO.getReservedSeats());
        bus.setDepartureTime(busDTO.getDepartureTime());
        bus.setDepartureDate(busDTO.getDepartureDate());
        bus.setArrivalDate(busDTO.getArrivalDate());
        bus.setArrivalTime(busDTO.getArrivalTime());
        bus.setDistance(busDTO.getDistance());
        bus.setTicketPrice(busDTO.getTicketPrice());

        return busRepository.save(bus);
    }

    @Override
    @Transactional
    public void updateBusSeatsAfterTicketDeletion(Map<Long, Integer> ticketsByBus) {
        List<Bus> busesToUpdate = busRepository.findAllById(ticketsByBus.keySet());
        for (Bus bus : busesToUpdate) {
            if (bus.getCapacity() > bus.getAvailableSeats()) {
                bus.setAvailableSeats(bus.getAvailableSeats() + ticketsByBus.get(bus.getId()));
                bus.setReservedSeats(bus.getReservedSeats() - ticketsByBus.get(bus.getId()));
            }
        }

        busRepository.saveAllAndFlush(busesToUpdate);
    }

    @Override
    @Transactional
    public void deleteBus(long id) {
        busRepository.deleteById(id);
    }

    private Bus buildBus(BusDTO busDTO) {
        Bus bus = new Bus();
        bus.setName(busDTO.getName());
        bus.setStartDestination(busDTO.getStartDestination());
        bus.setEndDestination(busDTO.getEndDestination());
        bus.setCapacity(busDTO.getCapacity());
        bus.setAvailableSeats(busDTO.getAvailableSeats());
        bus.setReservedSeats(busDTO.getReservedSeats());
        bus.setDepartureDate(busDTO.getDepartureDate());
        bus.setDepartureTime(busDTO.getDepartureTime());
        bus.setArrivalDate(busDTO.getArrivalDate());
        bus.setArrivalTime(busDTO.getArrivalTime());
        bus.setDistance(busDTO.getDistance());
        bus.setTicketPrice(busDTO.getTicketPrice());

        return bus;
    }

    private List<Bus> buildBuses(List<BusDTO> busDTOs) {
        List<Bus> buses = new ArrayList<>();
        for (BusDTO busDTO : busDTOs) {
            buses.add(buildBus(busDTO));
        }

        return buses;
    }

    private static List<Bus> unwrapBuses(List<Optional<Bus>> entity, Boolean searchWithTicketPrice) {
        List<Bus> result = new ArrayList<>();
        for (Optional<Bus> optionalBus : entity) {
            if (optionalBus.isPresent()) {
                result.add(optionalBus.get());
            }
        }

        if (result.isEmpty() && searchWithTicketPrice) {
            throw new EntityNotFoundException(BUS_NOT_FOUND_WITH_TICKET_PRICE_ERROR_MESSAGE);
        } else if (result.isEmpty()) {
            throw new EntityNotFoundException(BUS_NOT_FOUND_ERROR_MESSAGE);
        }

        return result;
    }

    private static Bus unwrapBus(Optional<Bus> entity, long id) {
        if (entity.isPresent()) return entity.get();
        else
            throw new EntityNotFoundException("We apologize, but we were unable to find any buses with this ID: " + id + " in our system.");
    }
}
