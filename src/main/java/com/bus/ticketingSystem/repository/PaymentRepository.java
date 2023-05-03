package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
