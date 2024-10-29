package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> makePayment(@Valid @RequestBody PaymentDTO paymentData) {
        return new ResponseEntity<>(paymentService.savePayment(paymentData), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getPaymentMethods(@RequestParam Long userId) {
        return new ResponseEntity<>(paymentService.getSavedPayments(userId), HttpStatus.OK);
    }
}
