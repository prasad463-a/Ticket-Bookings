package com.railway_system.Ticket.Booking.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
//@Table(name = "trains") // Maps this class to the 'trains' table in MySQL
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private int trainId;
    private Long trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private Integer totalSeats;
    private Integer availableSeats;
    
//    Train
//    |
//    |---- Booking1
//    |---- Booking2
//    |---- Booking3
   
    
    @JsonManagedReference("train-booking")
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    private List<Booking> bookings;
   
   
}