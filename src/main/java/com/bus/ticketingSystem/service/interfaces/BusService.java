package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.DTO.Reservation;

import java.util.List;

public interface BusService {
    List<Bus> getBusesByDestinationAndDate(Reservation reservation);
    Bus updateBusSeats(Long id, int reservedTickets);
}
