package com.bus.ticketingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NonNull
    @Column(name = "start_destination", nullable = false)
    private String startDestination;

    @NonNull
    @Column(name = "end_destination", nullable = false)
    private String endDestination;

    @NonNull
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @NonNull
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NonNull
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @NonNull
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NonNull
    @Column(name = "bus_name", nullable = false)
    private String busName;

    @NonNull
    @Column(nullable = false)
    private double price;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToOne(targetEntity = Ticket.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(targetEntity = Bus.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @NonNull
    @Column(name = "active", nullable = false)
    private boolean isActive;
}
