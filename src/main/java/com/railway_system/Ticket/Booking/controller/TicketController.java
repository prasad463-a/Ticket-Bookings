package com.railway_system.Ticket.Booking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.railway_system.Ticket.Booking.entity.Ticket;
import com.railway_system.Ticket.Booking.service.TicketService;

@RestController
@RequestMapping("/ticket")
public class TicketController {
     
     
     private final TicketService service;
     public TicketController(TicketService service) {
    	     this.service=service;
     }
     
     
     @PostMapping("/add/{bookingId}")
     public ResponseEntity<Ticket> generateTicket(@RequestBody Ticket ticket, @PathVariable int bookingId) {

         Ticket Ticket1 = service.generateTicket(bookingId, ticket);

         return new ResponseEntity<>(Ticket1, HttpStatus.CREATED);
     }

     // Get Ticket By Id
     @GetMapping("/get/{id}")
     public ResponseEntity<Optional<Ticket>> getTicket(@PathVariable int id) {

         Optional<Ticket> ticket = service.getTicketById(id);

         return new ResponseEntity<>(ticket, HttpStatus.OK);
     }

     // Get All Tickets
     @GetMapping("/getall")
     public ResponseEntity<List<Ticket>> getAllTickets() {

         List<Ticket> tickets = service.getAllTickets();

         return new ResponseEntity<>(tickets, HttpStatus.OK);
     }

     // Update Ticket
     @PutMapping("/update")
     public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket) {

         Ticket updatedTicket = service.updateTicket(ticket);

         return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
     }

     // Delete Ticket
     @DeleteMapping("/delete/{id}")
     public ResponseEntity<String> deleteTicket(@PathVariable int id) {

         service.deleteTicket(id);

         return new ResponseEntity<>("Ticket Deleted Successfully", HttpStatus.OK);
     }
 
     
	
}
