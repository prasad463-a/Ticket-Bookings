package com.railway_system.Ticket.Booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railway_system.Ticket.Booking.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

}
