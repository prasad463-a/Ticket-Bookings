package com.railway_system.Ticket.Booking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.railway_system.Ticket.Booking.dao.BookingDao;
import com.railway_system.Ticket.Booking.dao.TicketDao;
import com.railway_system.Ticket.Booking.entity.Booking;
import com.railway_system.Ticket.Booking.entity.Ticket;
import com.railway_system.Ticket.Booking.exception.BookingNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class TicketService {
	 
	private final BookingDao bookingdao;
     private final TicketDao ticketdao;
     public TicketService(TicketDao ticketdao,BookingDao bookingdao) {
    	     this.ticketdao=ticketdao;
    	     this.bookingdao=bookingdao;
     }
     @Transactional
     public Ticket generateTicket(int bookingId, Ticket ticket){

         Optional<Booking> optionalBooking =bookingdao.getBookingById(bookingId);

         if(optionalBooking.isEmpty()){

             throw new BookingNotFoundException("Booking Not Found");

         }

         Booking booking = optionalBooking.get();

          ticket.setPnr(UUID.randomUUID().toString().substring(0,10).toUpperCase());

          if (ticket.getCoach() == null || ticket.getCoach().trim().isEmpty()) {
              ticket.setCoach("S" + (1 + (bookingId % 5)));
          }

          if (ticket.getSeatNumber() == null || ticket.getSeatNumber().trim().isEmpty()) {
              int seatNum = (bookingId * 3) % 72 + 1;
              ticket.setSeatNumber(String.valueOf(seatNum));
          }

         Ticket savedTicket = ticketdao.generateTicket(ticket);

         booking.setTicket(savedTicket);

         bookingdao.updateBooking(booking);

         return savedTicket;

     }

     // Get Ticket By Id
     public Optional<Ticket> getTicketById(int id) {
         return ticketdao.getTicketById(id);
     }

     // Get All Tickets
     public List<Ticket> getAllTickets() {
         return ticketdao.getAllTickets();
     }

     // Update Ticket
     public Ticket updateTicket(Ticket ticket) {
         return ticketdao.updateTicket(ticket);
     }

     // Delete Ticket
     public void deleteTicket(int id) {
         ticketdao.deleteTicket(id);
     }
	 

}
