package com.railway_system.Ticket.Booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Train;

@Repository
public interface TrainRepository extends JpaRepository<Train, Integer> {
    
    // Industrial Feature: Custom Finder Method
    // Spring Boot reads this method name and automatically writes the SQL query for you!
    // SQL equivalent: SELECT * FROM trains WHERE source = ? AND destination = ?
    List<Train> findBySourceAndDestination(String source, String destination);



	
}
