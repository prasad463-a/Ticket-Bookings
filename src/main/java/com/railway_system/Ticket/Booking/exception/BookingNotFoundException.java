package com.railway_system.Ticket.Booking.exception;


public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }

}
