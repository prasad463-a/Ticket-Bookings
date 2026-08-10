package com.railway_system.Ticket.Booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Booking;



@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    // Industrial Feature: Find all bookings made by a specific user id
    // SQL equivalent: SELECT * FROM bookings WHERE user_id = ?
    List<Booking> findByUser_Id(int Id);
  
}