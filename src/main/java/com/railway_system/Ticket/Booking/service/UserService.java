package com.railway_system.Ticket.Booking.service;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.railway_system.Ticket.Booking.dao.UserDao;
import com.railway_system.Ticket.Booking.dto.LoginRequest;
import com.railway_system.Ticket.Booking.dto.LoginResponse;
import com.railway_system.Ticket.Booking.dto.UserRequest;
import com.railway_system.Ticket.Booking.dto.UserResponse;
import com.railway_system.Ticket.Booking.entity.User;
import com.railway_system.Ticket.Booking.exception.EmailAlreadyExistsException;
import com.railway_system.Ticket.Booking.exception.UserNotFoundException;
import com.railway_system.Ticket.Booking.jwt.JwtUtil;
import com.railway_system.Ticket.Booking.util.Entitydtoconverter;

@Service
public class UserService {

	private final BCryptPasswordEncoder passwordEncoder;
	private final UserDao userdao;
	private final JwtUtil jwtUtil;
	public UserService(UserDao userdao,BCryptPasswordEncoder passwordEncoder,JwtUtil jwtUtil) {
		this.userdao=userdao;	
		this.passwordEncoder=passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public UserResponse register(UserRequest request) {

	    Optional<User> optional =userdao.getUserByEmail(request.getEmail());

	    if(optional.isPresent()) {
	        throw new EmailAlreadyExistsException("Email Already Exists");
	    }

	    User user =Entitydtoconverter.convertToEntity(request);
        
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    User savedUser = userdao.saveUser(user);

	    return Entitydtoconverter.convertToResponse(savedUser);

	}


	public UserResponse getUserById(int id) {

	    User user = userdao.getUserById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));

	    return Entitydtoconverter.convertToResponse(user);

	}

	public List<UserResponse> getAllUser() {

	    List<User> users = userdao.getAllUser();

	    return users.stream().map(Entitydtoconverter ::convertToResponse).toList();

	}

	public UserResponse updateUser(UserRequest request,int id) {

	    User user = userdao.getUserById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));

	    if (request.getName() != null && !request.getName().trim().isEmpty()) {
	        user.setName(request.getName());
	    }
	    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
	        user.setEmail(request.getEmail());
	    }
	    if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
	        user.setPhone(request.getPhone());
	    }
	    if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
	        user.setPassword(passwordEncoder.encode(request.getPassword()));
	    }

	    User updated = userdao.updateUser(user);

	    return Entitydtoconverter.convertToResponse(updated);

	}

	public void delete(int id) {
		
	      userdao.delete(id);;
	}

	public UserResponse getUserByEmail(String email) {
		
		User user = userdao.getUserByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));
		
		return Entitydtoconverter.convertToResponse(user);
				
	}
	public LoginResponse login(LoginRequest request) {

	    User user = userdao.getUserByEmail(request.getEmail())
	            .orElseThrow(() -> new UserNotFoundException("Invalid Email"));

	    // If user's password in database is NULL or empty, set it to the typed password
	    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
	        String inputPassword = request.getPassword();
	        if (inputPassword == null || inputPassword.trim().isEmpty()) {
	            throw new RuntimeException("Password cannot be empty");
	        }
	        user.setPassword(passwordEncoder.encode(inputPassword));
	        userdao.updateUser(user);
	    }

	    boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

	    // Fallback: If password in DB is plain-text (e.g. manual DB entry)
	    if (!passwordMatches && request.getPassword().equals(user.getPassword())) {
	        passwordMatches = true;
	        // Auto-upgrade password to BCrypt hash in database
	        user.setPassword(passwordEncoder.encode(request.getPassword()));
	        userdao.updateUser(user);
	    }

	    if (!passwordMatches) {
	        throw new RuntimeException("Invalid Password");
	    }
	    
	    String token = jwtUtil.generateToken(user.getEmail());
	    LoginResponse response = new LoginResponse();

	    response.setId(user.getId());
	    response.setName(user.getName());
	    response.setEmail(user.getEmail());
	    response.setRole(user.getRole());
	    response.setMessage("Login Successful");
	    response.setToken(token);

	    return response;
	}

	
	
	
}
