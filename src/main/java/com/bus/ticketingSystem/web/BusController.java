package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.entity.Bus;
import com.bus.ticketingSystem.entity.Reservation;
import com.bus.ticketingSystem.service.interfaces.BusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/buses")
public class BusController {
    private BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public ResponseEntity<List<Bus>> getBuses(@RequestParam String start, @RequestParam String end, @RequestParam LocalDate date) {
        Reservation reservation = new Reservation(start, end, date);
        System.out.println("START: " + reservation.getStartDestination());
        System.out.println("END: " + reservation.getEndDestination());
        System.out.println("DATE: " + reservation.getDate());

        return new ResponseEntity<>(busService.getBusesByDestinationAndDate(reservation), HttpStatus.OK);
    }
}
