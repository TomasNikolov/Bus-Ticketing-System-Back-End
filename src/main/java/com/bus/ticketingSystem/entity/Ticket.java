package com.bus.ticketingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @NonNull
    @Column(nullable = false)
    private String source;

    @NonNull
    @Column(nullable = false)
    private String destination;

    @NonNull
    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @NonNull
    @Column(name = "bus_id", nullable = false)
    private long busId;

    @NonNull
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;
}
