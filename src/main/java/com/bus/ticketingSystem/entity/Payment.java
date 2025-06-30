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
    @Column
    private long id;

    @NonNull
    @Column(name = "transaction_id")
    private String transactionId;

    @NonNull
    @Column(name = "card_number", nullable = false)
    private long cardNumber;

    @NonNull
    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @NonNull
    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(name = "payment_token", nullable = false)
    private String paymentToken;

    @NonNull
    @Column(nullable = false)
    private double amount;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "card_first_digit")
    private int cardFirstDigit;
}
