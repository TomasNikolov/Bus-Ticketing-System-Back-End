package com.bus.ticketingSystem.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private long cardNumber;
    private String cardHolder;
    private String expiryDate;
    private int cvv;
    private double amount;
    private long userId;
}
