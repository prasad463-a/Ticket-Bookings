package com.railway_system.Ticket.Booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    
	 @NotBlank(message = "Name is required")
    private String name;
    
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
    private String email;
	@Size(min = 6, message = "Password must contain at least 6 characters")
    private String password;
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
    private String phone;
//    private String role;

}
