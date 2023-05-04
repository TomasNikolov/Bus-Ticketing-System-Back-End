package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query(value = "SELECT * FROM ticket WHERE ticket.user_id = ?1", nativeQuery = true)
    List<Ticket> findTicketsByUserId(long userId);
}
