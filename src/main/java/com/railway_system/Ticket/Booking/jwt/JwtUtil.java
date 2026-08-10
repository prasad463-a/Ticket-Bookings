package com.railway_system.Ticket.Booking.jwt;

import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;



import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Secret key (keep it safe; later we'll move it to application.properties)
    private static final String SECRET =
            "MyRailwayBookingProjectSecretKeyForJwtAuthentication123456";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token validity: 24 hours
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Generate JWT Token
    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }
    public boolean isTokenExpired(String token) {

        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.before(new Date());

    }
    public boolean validateToken(String token, String email) {

        String extractedEmail = extractEmail(token);

        return extractedEmail.equals(email) && !isTokenExpired(token);

    }

}
