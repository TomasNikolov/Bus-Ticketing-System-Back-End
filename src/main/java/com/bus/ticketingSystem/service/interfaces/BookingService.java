package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.BookingDTO;
import com.bus.ticketingSystem.entity.Booking;

import java.util.List;
import java.util.Set;

public interface BookingService {
    List<Booking> createBookings(long userId);
    List<BookingDTO> getBookingsByUserId(long userId);
}
