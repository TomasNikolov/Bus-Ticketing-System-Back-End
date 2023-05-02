package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/reserve-ticket")
    public ResponseEntity<List<Ticket>> reserveTicket(@Valid @RequestBody List<TicketDTO> tickets) {
        return new ResponseEntity<>(bookingService.reserveTickets(tickets), HttpStatus.OK);
    }
}
