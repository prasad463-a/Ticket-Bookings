package com.railway_system.Ticket.Booking.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<String> userNotFound(UserNotFoundException ex) {
//
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//
//    }

    @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<String> trainNotFound(TrainNotFoundException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> bookingNotFound(BookingNotFoundException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<String> seatNotAvailable(SeatNotAvailableException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex,HttpServletRequest request){
		ApiError error = new ApiError();
		error.setStatus(HttpStatus.NOT_FOUND.value());
	    error.setMessage(ex.getMessage());
	    error.setPath(request.getRequestURI());
	    error.setTimeStamp(LocalDateTime.now());
	    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleEmailAlreadyExistsException(
	        EmailAlreadyExistsException ex,
	        HttpServletRequest request) {

	    ApiError error = new ApiError();

	    error.setStatus(HttpStatus.CONFLICT.value());
	    error.setMessage(ex.getMessage());
	    error.setPath(request.getRequestURI());
	    error.setTimeStamp(LocalDateTime.now());

	    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(
	        MethodArgumentNotValidException ex,
	        HttpServletRequest request) {

	    Map<String, String> validationErrors = new HashMap<>();

	    ex.getBindingResult().getFieldErrors().forEach(error -> {
	        validationErrors.put(error.getField(), error.getDefaultMessage());
	    });

	    ApiError apiError = new ApiError();

	    apiError.setStatus(HttpStatus.BAD_REQUEST.value());
	    apiError.setMessage("Validation Failed");
	    apiError.setPath(request.getRequestURI());
	    apiError.setTimeStamp(LocalDateTime.now());
	    apiError.setErrors(validationErrors);

	    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiError> handleRuntimeException(
	        RuntimeException ex,
	        HttpServletRequest request) {

	    ApiError error = new ApiError();

	    error.setStatus(HttpStatus.UNAUTHORIZED.value());
	    error.setMessage(ex.getMessage());
	    error.setPath(request.getRequestURI());
	    error.setTimeStamp(LocalDateTime.now());

	    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

}