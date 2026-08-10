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

import com.railway_system.Ticket.Booking.entity.Booking;
import com.railway_system.Ticket.Booking.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {
	
     private  final BookingService service;


     public BookingController(BookingService service) {
         this.service = service;
     }
     
     @PostMapping("/book")
     public ResponseEntity<Booking> bookTicket(@RequestBody Booking booking) {

         Booking savedBooking = service.bookTicket(booking);

         return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
     }

     @GetMapping("/get/{id}")
     public ResponseEntity<Optional<Booking>> getBooking(@PathVariable int id) {

         Optional<Booking> booking = service.getBookingById(id);

         return new ResponseEntity<>(booking, HttpStatus.OK);
     }

     @GetMapping("/getall")
     public ResponseEntity<List<Booking>> getAllBookings() {

         List<Booking> bookings = service.getAllBookings();

         return new ResponseEntity<>(bookings, HttpStatus.OK);
     }

     @GetMapping("/user/{userId}")
     public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable int userId) {

         List<Booking> bookings = service.getBookingsByUserId(userId);

         return new ResponseEntity<>(bookings, HttpStatus.OK);
     }

     @PutMapping("/update")
     public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {

         Booking updatedBooking = service.updateBooking(booking);

         return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
     }

     @DeleteMapping("/delete/{id}")
     public ResponseEntity<String> deleteBooking(@PathVariable int id) {

         service.deleteBooking(id);

         return new ResponseEntity<>("Booking Deleted Successfully", HttpStatus.OK);
     }
     @DeleteMapping("/cancel/{id}")
     public ResponseEntity<Booking> cancelBooking(@PathVariable int id) {

         Booking booking = service.cancelBooking(id);

         return new ResponseEntity<>(booking, HttpStatus.OK);
     }
}
