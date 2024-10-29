package com.bus.ticketingSystem.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    @NotNull(message = "Card number is required")
    @Digits(integer = 16, fraction = 0, message = "Card number must be 16 digits")
    private long cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Size(min = 1, max = 50, message = "Card holder name must be between 1 and 50 characters")
    private String cardHolder;

    @NotNull(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format")
    private String expiryDate;

    @NotNull(message = "CVV is required")
    @Digits(integer = 3, fraction = 0, message = "CVV must be 3 digits")
    private int cvv;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private double amount;

    private long userId;

    private Boolean saveForFutureUse;

    private String paymentToken;

    private long id;

    private int cardFirstDigit;
}
