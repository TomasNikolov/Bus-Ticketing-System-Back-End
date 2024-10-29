package com.bus.ticketingSystem.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
public class Reservation {
    private String startDestination;

    private String endDestination;

    private LocalDate date;

    private double maxTicketPrice;

    public Reservation(String startDestination, String endDestination, LocalDate date) {
        this.startDestination = startDestination;
        this.endDestination = endDestination;
        this.date = date;
    }

    public Reservation(String startDestination, String endDestination, LocalDate date, double maxTicketPrice) {
        this.startDestination = startDestination;
        this.endDestination = endDestination;
        this.date = date;
        this.maxTicketPrice = maxTicketPrice;
    }
}
