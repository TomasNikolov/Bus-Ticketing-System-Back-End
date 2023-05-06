package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM bookings WHERE bookings.user_id = ?1", nativeQuery = true)
    List<Booking> findBookingByUserId(long userId);
}
