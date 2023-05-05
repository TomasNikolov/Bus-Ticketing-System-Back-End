package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.TicketDTO;
import com.bus.ticketingSystem.entity.Ticket;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/send")
    public ResponseEntity<?> sendTicketByEmail(@RequestParam Long ticketId) {
        ticketService.sendTicket(ticketId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reserve")
    public ResponseEntity<List<Ticket>> reserveTicket(@Valid @RequestBody List<TicketDTO> tickets) {
        return new ResponseEntity<>(ticketService.reserveTickets(tickets), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteTickets(@Valid @RequestBody Set<Long> ids) {
        ticketService.deleteTickets(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
