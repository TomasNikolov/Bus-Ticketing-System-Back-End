package com.bus.ticketingSystem.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BusDTO {
    private long id;
    private String name;
    private String startDestination;
    private String endDestination;
    private int capacity;
    private int availableSeats;
    private int reservedSeats;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private double distance;
    private double ticketPrice;
}
