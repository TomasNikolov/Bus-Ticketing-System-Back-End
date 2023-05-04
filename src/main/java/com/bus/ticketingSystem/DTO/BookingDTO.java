package com.bus.ticketingSystem.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingDTO {
    private long id;
    private String startDestination;
    private String endDestination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String busName;
    private double price;
    private String status;
}
