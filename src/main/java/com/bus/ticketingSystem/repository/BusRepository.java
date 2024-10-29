package com.bus.ticketingSystem.repository;

import com.bus.ticketingSystem.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    @Query(value = "SELECT * FROM bus WHERE bus.start_destination = ?1 AND bus.end_destination = ?2 AND bus.departure_date = ?3 AND bus.available_seats > 0 ORDER BY bus.departure_date DESC", nativeQuery = true)
    List<Optional<Bus>> findBusByRouteAndDate(String startDestination, String endDestination, LocalDate date);

    @Query(value = "SELECT * FROM bus WHERE bus.start_destination = ?1 AND bus.end_destination = ?2 AND bus.departure_date = ?3 AND bus.ticket_price <= ?4 AND bus.available_seats > 0 ORDER BY bus.ticket_price ASC", nativeQuery = true)
    List<Optional<Bus>> findBusByRouteDateAndTicketPrice(String startDestination, String endDestination,
                                                         LocalDate date, double maxTicketPrice);
}
