package com.railway_system.Ticket.Booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.railway_system.Ticket.Booking.enums.BookingStatus;
import com.railway_system.Ticket.Booking.enums.PaymentMode;
import com.railway_system.Ticket.Booking.enums.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
@Data
@Entity
public class Payment {
	 @Id // Marks this field as the Primary Key
	    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to Auto-Increment (1, 2, 3...)
	    private int id;
        private Double amount;
        @Enumerated(EnumType.STRING)
        private PaymentMode paymentMode;
        @Enumerated(EnumType.STRING)
        private PaymentStatus paymentStatus;
        
//        Booking1 <-------> Payment1
//        Booking2 <-------> Payment2
        @JsonBackReference("paymeny-booking")
        @OneToOne(mappedBy = "payment")
        private Booking booking;
	   
}
