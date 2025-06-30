package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);

    List<ConfirmationToken> findByUserId(Long userId);
}
