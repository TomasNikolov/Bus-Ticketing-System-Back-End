package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.BookingDTO;
import com.bus.ticketingSystem.DTO.BusDTO;
import com.bus.ticketingSystem.DTO.UserDTO;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.BusService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import com.bus.ticketingSystem.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private BusService busService;
    private BookingService bookingService;
    private TicketService ticketService;
    private UserService userService;

    @GetMapping("/buses")
    public ResponseEntity<?> getBuses() {
        return new ResponseEntity<>(busService.getAllBuses(), HttpStatus.OK);
    }

    @PostMapping("/buses")
    public ResponseEntity<?> createBus(@Valid @RequestBody BusDTO busDTO) {
        System.out.println("CREATE BUS HAS BEEN INVOKED");
        busService.createBus(busDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/buses/bulk")
    public ResponseEntity<?> createBuses(@Valid @RequestBody List<BusDTO> busDTOs) {
        System.out.println("CREATE BUSES HAS BEEN INVOKED");
        busService.createBuses(busDTOs);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/buses")
    public ResponseEntity<?> updateBus(@Valid @RequestBody BusDTO busDTO) {
        busService.updateBus(busDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/buses")
    public ResponseEntity<?> deleteBus(@RequestParam Long id) {
        ticketService.deleteTicketsByBusId(id);
        bookingService.deleteBookingsByBusId(id);
        busService.deleteBus(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings() {
        return new ResponseEntity<>(bookingService.getAllBookings(), HttpStatus.OK);
    }

    @PutMapping("/bookings")
    public ResponseEntity<?> updateBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        bookingService.updateBooking(bookingDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/bookings")
    public ResponseEntity<?> deleteBooking(@RequestParam Long id) {
        bookingService.cancelBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        bookingService.deleteBookings(id);
        ticketService.deleteTicketsByUserId(id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
