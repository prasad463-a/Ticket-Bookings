package com.railway_system.Ticket.Booking.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
//@Table(name = "users") // Maps this Java class to a table named 'users' in MySQL
public class User {

    @Id // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells MySQL to Auto-Increment (1, 2, 3...)
    private int id;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    
//    User
//    |
//    |------ Booking1
//    |------ Booking2
//    |------ Booking3
    @ToString.Exclude
    @JsonManagedReference("user-booking")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;

	
	
    
    
}