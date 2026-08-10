package com.railway_system.Ticket.Booking.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Ticket;
import com.railway_system.Ticket.Booking.repository.TicketRepository;

@Repository
public class TicketDao {

    private final TicketRepository repository;

    public TicketDao(TicketRepository repository) {
        this.repository = repository;
    }

    public Ticket generateTicket(Ticket ticket) {
        return repository.save(ticket);
    }

    public Optional<Ticket> getTicketById(int id) {
        return repository.findById(id);
    }

    public List<Ticket> getAllTickets() {
        return repository.findAll();
    }

    public Ticket updateTicket(Ticket ticket) {
        return repository.save(ticket);
    }

    public void deleteTicket(int id) {
        repository.deleteById(id);
    }

	

	

	

}
