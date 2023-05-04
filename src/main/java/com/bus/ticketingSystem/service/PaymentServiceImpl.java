package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.entity.Payment;
import com.bus.ticketingSystem.repository.PaymentRepository;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;
    private BookingService bookingService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingService bookingService) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public Payment savePayment(PaymentDTO paymentData) {
        Payment payment = paymentRepository.save(getPayment(paymentData));
        bookingService.createBookings(paymentData.getUserId());
        return payment;
    }

    private static Payment getPayment(PaymentDTO paymentData) {
        Payment payment = new Payment();
        payment.setCardNumber(paymentData.getCardNumber());
        payment.setCardHolder(paymentData.getCardHolder());
        payment.setExpiryDate(paymentData.getExpiryDate());
        payment.setCvv(paymentData.getCvv());
        payment.setAmount(paymentData.getAmount());
        return payment;
    }
}
