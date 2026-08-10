package com.railway_system.Ticket.Booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity

public class Ticket {
	 @Id // Marks this field as the Primary Key
	    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to Auto-Increment (1, 2, 3...)
	    private int id;

	    private String pnr;
	    private Double fare;
	    private String journeyDate;
	    private String coach;
	    private String seatNumber;
	   
//	    Booking1 <-------> Ticket1
//	    Booking2 <-------> Ticket2
	    @JsonBackReference("ticket-booking")
	    @OneToOne(mappedBy="ticket")
	    private Booking booking;

}
