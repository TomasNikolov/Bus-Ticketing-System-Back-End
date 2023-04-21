package com.bus.ticketingSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private long transactionId;

    @NonNull
    @Column(name = "card_number", nullable = false)
    private long cardNumber;

    @NonNull
    @Column(nullable = false)
    private String bank;

    @NonNull
    @Column(nullable = false)
    private double amount;

}
