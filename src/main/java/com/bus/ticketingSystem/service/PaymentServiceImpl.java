package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.entity.Payment;
import com.bus.ticketingSystem.repository.PaymentRepository;
import com.bus.ticketingSystem.service.interfaces.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment savePayment(PaymentDTO paymentData) {
        Payment payment = new Payment();
        payment.setCardNumber(paymentData.getCardNumber());
        payment.setCardHolder(paymentData.getCardHolder());
        payment.setExpiryDate(paymentData.getExpiryDate());
        payment.setCvv(paymentData.getCvv());
        payment.setAmount(paymentData.getAmount());

        return paymentRepository.save(payment);
    }
}
