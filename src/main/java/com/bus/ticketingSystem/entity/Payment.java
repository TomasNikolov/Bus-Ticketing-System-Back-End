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
    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @NonNull
    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @NonNull
    @Column(nullable = false)
    private int cvv;

    @NonNull
    @Column(nullable = false)
    private double amount;

}
