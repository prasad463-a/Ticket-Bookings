package com.railway_system.Ticket.Booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.railway_system.Ticket.Booking.enums.BookingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Booking {
	  @Id // Marks this field as the Primary Key
	    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to Auto-Increment (1, 2, 3...)
	    private int id;

	    private String bookingDate;
	    private Integer numSeatBooked;
	    @Enumerated(EnumType.STRING)
	    private BookingStatus status;
	    
//	    Booking1 -----> User
//	    Booking2 -----> User
//	    Booking3 -----> User
	    
	    @JsonBackReference("user-booking")
	    @ManyToOne
	    private User user;
	    
//	    Booking1 -----> Train
//	    Booking2 -----> Train
//	    Booking3 -----> Train
	    
	    @JsonBackReference("train-booking")
	    @ManyToOne
	    private Train train;
	    
//	    Booking1 <-------> Ticket1
//	    Booking2 <-------> Ticket2
	    
	    @JsonManagedReference("ticket-booking")
	    @OneToOne(cascade = CascadeType.ALL)
	    private Ticket ticket;
	    
//	    Booking1 <-------> Payment1
//	    Booking2 <-------> Payment2
	    @JsonManagedReference("paymeny-booking")
	    @OneToOne(cascade = CascadeType.ALL)
	    private Payment payment;
	
	

}
