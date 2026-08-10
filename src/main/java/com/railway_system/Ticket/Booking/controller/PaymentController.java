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

import com.railway_system.Ticket.Booking.entity.Payment;
import com.railway_system.Ticket.Booking.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	
     private final PaymentService service;
     public PaymentController(PaymentService service) {
    	   this.service=service; 
     }
     @PostMapping("/add/{id}")
     public ResponseEntity<Payment> savePayment(@RequestBody Payment payment, @PathVariable int id) {

         Payment payment1 = service.savePayment(payment,id);

         return new ResponseEntity<>(payment1, HttpStatus.CREATED);
     }
     // Get Payment By Id
     @GetMapping("/get/{id}")
     public ResponseEntity<Optional<Payment>> getPayment(@PathVariable int id) {

         Optional<Payment> payment = service.getPaymentById(id);

         return new ResponseEntity<>(payment, HttpStatus.OK);
     }

     // Get All Payments
     @GetMapping("/getall")
     public ResponseEntity<List<Payment>> getAllPayments() {

         List<Payment> payments = service.getAllPayments();

         return new ResponseEntity<>(payments, HttpStatus.OK);
     }

     // Update Payment
     @PutMapping("/update")
     public ResponseEntity<Payment> updatePayment(@RequestBody Payment payment) {

         Payment updatedPayment = service.updatePayment(payment);

         return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
     }

     // Delete Payment
     @DeleteMapping("/delete/{id}")
     public ResponseEntity<String> deletePayment(@PathVariable int id) {

         service.deletePayment(id);

         return new ResponseEntity<>("Payment Deleted Successfully", HttpStatus.OK);
     }
	
	
}
