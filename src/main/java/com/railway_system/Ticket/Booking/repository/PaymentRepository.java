package com.railway_system.Ticket.Booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railway_system.Ticket.Booking.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer>{

}
