package com.bus.ticketingSystem.service.interfaces;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment savePayment(PaymentDTO paymentData);
    List<PaymentDTO> getSavedPayments(long userId);
}
