package com.bus.ticketingSystem.web;

import com.bus.ticketingSystem.DTO.PaymentDTO;
import com.bus.ticketingSystem.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
