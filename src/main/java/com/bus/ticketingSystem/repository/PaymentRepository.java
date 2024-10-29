package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT * FROM payment WHERE user_id = ?1 AND payment_token IS NOT NULL AND card_first_digit IS NOT NULL", nativeQuery = true)
    List<Payment> getPaymentsByUserId(long userId);
}
