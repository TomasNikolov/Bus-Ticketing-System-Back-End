package com.bus.ticketingSystem.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketDTO {
    private String startDestination;

    private String endDestination;

    private String passengerName;

    private long busId;

    private int busCapacity;

    private int reservedTickets;
}
