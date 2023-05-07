package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.BusDTO;
import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.DTO.Reservation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BusService {
    List<Bus> getBusesByDestinationAndDate(Reservation reservation);

    Bus updateBusSeats(Long id, int reservedTickets);

    List<Bus> getBussesByIds(Set<Long> ids);

    Bus getBusById(long id, List<Bus> buses);

    void updateBusSeatsAfterBookingCancellation(Long id);

    Bus getBusById(long id);

    List<Bus> getAllBuses();

    Bus createBus(BusDTO busDTO);

    Bus updateBus(BusDTO busDTO);

    void updateBusSeatsAfterTicketDeletion(Map<Long, Integer> ticketsByBus);

    void deleteBus(long id);
}
