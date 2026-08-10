package com.railway_system.Ticket.Booking.util;

import java.util.Optional;

import com.railway_system.Ticket.Booking.dto.UserRequest;
import com.railway_system.Ticket.Booking.dto.UserResponse;
import com.railway_system.Ticket.Booking.entity.User;

public class Entitydtoconverter {
	 public static User convertToEntity(UserRequest request) {

	        User user = new User();

	        user.setName(request.getName());
	        user.setEmail(request.getEmail());
	        user.setPassword(request.getPassword());
	        user.setPhone(request.getPhone());
	        user.setRole("ROLE_USER");
	        return user;
	    }

	    public static UserResponse convertToResponse(User user) {
	        UserResponse response = new UserResponse();
	        response.setId(user.getId());
	        response.setName(user.getName());
	        response.setEmail(user.getEmail());
	        response.setPhone(user.getPhone());
	        response.setRole(user.getRole());
	        return response;
	    }

		

		
}
