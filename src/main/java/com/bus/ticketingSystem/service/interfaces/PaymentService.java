package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.entity.Payment;

public interface PaymentService {
    Payment savePayment(PaymentDTO paymentData);
}
