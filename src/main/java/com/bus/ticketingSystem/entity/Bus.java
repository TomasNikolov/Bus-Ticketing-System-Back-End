package com.bus.ticketingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(name = "start_destination", nullable = false)
    private String startDestination;

    @NonNull
    @Column(name = "end_destination", nullable = false)
    private String endDestination;

    @NonNull
    @Column(nullable = false)
    private int capacity;

    @NonNull
    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @NonNull
    @Column(name = "reserved_seats", nullable = false)
    private int reservedSeats;

    @NonNull
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @NonNull
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @NonNull
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @NonNull
    @Column(nullable = false)
    private double distance;
}
