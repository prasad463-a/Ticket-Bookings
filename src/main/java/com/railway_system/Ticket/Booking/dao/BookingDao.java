package com.railway_system.Ticket.Booking.dao;



import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Booking;
import com.railway_system.Ticket.Booking.repository.BookingRepository;

@Repository
public class BookingDao {

    private final BookingRepository repository;

    public BookingDao(BookingRepository repository) {
        this.repository = repository;
    }

    public Booking bookTicket(Booking booking) {
        return repository.save(booking);
    }

    public Optional<Booking> getBookingById(int id) {
        return repository.findById(id);
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    public List<Booking> getBookingsByUserId(int userId) {
        return repository.findByUser_Id(userId);
    }

    public Booking updateBooking(Booking booking) {
        return repository.save(booking);
    }

    public void deleteBooking(int id) {
        repository.deleteById(id);
    }

	
	


	


}