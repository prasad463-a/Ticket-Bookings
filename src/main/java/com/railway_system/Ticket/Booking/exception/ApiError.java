package com.railway_system.Ticket.Booking.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
@Data
public class ApiError {
	
	 private int status;
	 private String message;
	 private String path;
	 private LocalDateTime timeStamp;
	 private Map<String, String> errors;
}
