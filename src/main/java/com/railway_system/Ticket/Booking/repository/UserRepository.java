package com.railway_system.Ticket.Booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railway_system.Ticket.Booking.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	

//	Optional<User> findById(int id);
//    // You don't need to write any code here yet! 
//    // Inheriting JpaRepository gives us all basic CRUD operations automatically.
}
