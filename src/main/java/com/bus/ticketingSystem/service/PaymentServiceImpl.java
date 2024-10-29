package com.bus.ticketingSystem.service;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.entity.Payment;
import com.bus.ticketingSystem.repository.PaymentRepository;
import com.bus.ticketingSystem.service.interfaces.BookingService;
import com.bus.ticketingSystem.service.interfaces.PaymentService;
import com.bus.ticketingSystem.service.interfaces.TicketService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final long NEGATIVE_BALANCE_CARD = 4545454545454545L;
    private PaymentRepository paymentRepository;
    private BookingService bookingService;
    private TicketService ticketService;
    private final Validator validator;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingService bookingService, TicketService ticketService) {
        this.paymentRepository = paymentRepository;
        this.bookingService = bookingService;
        this.ticketService = ticketService;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    @Transactional
    public Payment savePayment(PaymentDTO paymentData) {
        if (paymentData.getPaymentToken() == null) {
            this.validatePaymentData(paymentData);
        }
        Payment payment = paymentRepository.save(this.getPayment(paymentData));
        bookingService.createBookings(paymentData.getUserId());
        ticketService.payTickets(paymentData.getUserId());
        return payment;
    }

    @Override
    public List<PaymentDTO> getSavedPayments(long userId) {
        return this.parsePayments(paymentRepository.getPaymentsByUserId(userId));
    }

    private List<PaymentDTO> parsePayments(List<Payment> payments) {
        return payments.stream()
                .map(payment -> {
                    PaymentDTO paymentDTO = new PaymentDTO();
                    paymentDTO.setPaymentToken(payment.getPaymentToken());
                    paymentDTO.setCardNumber(payment.getCardNumber());
                    paymentDTO.setId(payment.getId());
                    paymentDTO.setCardHolder(payment.getCardHolder());
                    paymentDTO.setExpiryDate(payment.getExpiryDate());
                    paymentDTO.setCardFirstDigit(payment.getCardFirstDigit());
                    return paymentDTO;
                })
                .collect(Collectors.toList());
    }

    private void validatePaymentData(PaymentDTO paymentData) {
        Set<ConstraintViolation<PaymentDTO>> violations = validator.validate(paymentData);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<PaymentDTO> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Payment validation failed: \n" + sb.toString());
        }

        if (paymentData.getCardNumber() == NEGATIVE_BALANCE_CARD) {
            throw new IllegalArgumentException("Transaction failed: The available balance on your card is insufficient to complete this payment.");
        }
    }

    private Payment getPayment(PaymentDTO paymentData) {
        Payment payment = new Payment();
        if (paymentData.getPaymentToken() == null) {
            payment.setCardNumber(this.getLast4Digits(paymentData.getCardNumber()));
            payment.setCardFirstDigit(this.getCardFirstDigit(paymentData.getCardNumber()));
        } else {
            payment.setCardNumber(paymentData.getCardNumber());
            payment.setPaymentToken(paymentData.getPaymentToken());
        }
        payment.setCardHolder(paymentData.getCardHolder());
        payment.setExpiryDate(paymentData.getExpiryDate());
        payment.setAmount(paymentData.getAmount());
        payment.setUserId(paymentData.getUserId());
        payment.setTransactionId(this.generateTransactionId());

        if (paymentData.getSaveForFutureUse()) {
            payment.setPaymentToken(this.generatePaymentToken(paymentData.getCardNumber(), paymentData.getCvv()));
        }

        return payment;
    }

    private int getCardFirstDigit(long cardNumber) {
        String cardNumberStr = Long.toString(cardNumber);
        String firstTwoDigits = cardNumberStr.substring(0, 2);
        return Integer.parseInt(firstTwoDigits);
    }

    private long getLast4Digits(long cardNumber) {
        String last4Digits = String.valueOf(cardNumber).substring(String.valueOf(cardNumber).length() - 4);
        return Long.parseLong(last4Digits);
    }

    private String generatePaymentToken(long cardNumber, int cvv) {
        try {
            String input = cardNumber + ":" + cvv;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
