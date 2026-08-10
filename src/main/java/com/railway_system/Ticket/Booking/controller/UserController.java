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

import com.railway_system.Ticket.Booking.dto.LoginRequest;
import com.railway_system.Ticket.Booking.dto.LoginResponse;
import com.railway_system.Ticket.Booking.dto.UserRequest;
import com.railway_system.Ticket.Booking.dto.UserResponse;
import com.railway_system.Ticket.Booking.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {
	

	private final UserService service;
	
	public UserController(UserService service) {
		this.service=service;
	}
	
	  // Register User
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {

	    UserResponse response = service.register(request);

	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
    
    

    @GetMapping("/get/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable int id) {

        UserResponse response = service.getUserById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @GetMapping("/getalluser")
    public ResponseEntity<List<UserResponse>> getAllUser() {

        List<UserResponse> users = service.getAllUser();

        return new ResponseEntity<>(users,HttpStatus.OK);

    }
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable int id,@RequestBody UserRequest request){

        UserResponse response =service.updateUser(request,id);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    // Delete User
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {

        service.delete(id);

        return new ResponseEntity<>("User Deleted Successfully", HttpStatus.OK);

    }
    
    @GetMapping("/getbyemail/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {

        UserResponse user = service.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = service.login(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    

}
