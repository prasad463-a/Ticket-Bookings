package com.railway_system.Ticket.Booking.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.railway_system.Ticket.Booking.entity.User;
import com.railway_system.Ticket.Booking.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserRepository userRepository) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

    	String authHeader = request.getHeader("Authorization");

    	System.out.println("==================================");
    	System.out.println("Request URI : " + request.getRequestURI());
    	System.out.println("Authorization Header : " + authHeader);

    	if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    	    System.out.println("No Bearer token found");
    	    filterChain.doFilter(request, response);
    	    return;
    	}

    	String token = authHeader.substring(7).trim();

    	System.out.println("Token = [" + token + "]");
    	System.out.println("Token Length = " + token.length());

    	if (token.isEmpty()) {
    	    System.out.println("JWT Token is EMPTY");
    	    filterChain.doFilter(request, response);
    	    return;
    	}

    	System.out.println("Token = [" + token + "]");
    	System.out.println("Token Length = " + token.length());

    	String email;

    	try {
    	    email = jwtUtil.extractEmail(token);
    	    System.out.println("Email = " + email);
    	} catch (Exception e) {
    	    System.out.println("JWT Error = " + e.getMessage());
    	    filterChain.doFilter(request, response);
    	    return;
    	}

    	User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && jwtUtil.validateToken(token, email)) {

            UsernamePasswordAuthenticationToken authentication =
            		 new UsernamePasswordAuthenticationToken(
                             user,
                             null,
                             List.of(new SimpleGrantedAuthority(user.getRole()))
                     );
            System.out.println("Role from DB = " + user.getRole());
            System.out.println("Authority Created = " + authentication.getAuthorities());

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
//            SecurityContextHolder.getContext()
//            .setAuthentication(authentication);

  
    System.out.println("Authentication Stored Successfully");
    System.out.println("Authorities : "
            + SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities());
        }

        filterChain.doFilter(request, response);
    }
}
