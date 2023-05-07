package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.BookingDTO;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private TicketService ticketService;
    private BookingService bookingService;

    public BookingController(TicketService ticketService, BookingService bookingService) {
        this.ticketService = ticketService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getBookings(@RequestParam Long userId) {
        return new ResponseEntity<>(bookingService.getBookingsByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> cancelBooking(@RequestParam Long id) {
        bookingService.cancelBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
