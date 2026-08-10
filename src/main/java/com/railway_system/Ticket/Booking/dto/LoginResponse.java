package com.railway_system.Ticket.Booking.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private int id;
    private String name;
    private String email;
    private String role;
    private String message;
    
    private String token;

}