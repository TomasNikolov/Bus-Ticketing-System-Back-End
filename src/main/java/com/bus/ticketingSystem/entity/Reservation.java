package com.bus.ticketingSystem.entity;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
public class Reservation {
    private String startDestination;

    private String endDestination;

    private LocalDate date;

    public Reservation(String startDestination, String endDestination, LocalDate date) {
        this.startDestination = startDestination;
        this.endDestination = endDestination;
        this.date = date;
    }
}
