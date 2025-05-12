package com.inventorymanagement.usermodule.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.inventorymanagement.usermodule.entity.User;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

//    @Value("${jwt.secret}")
//    private String secretKey;
    
    private static final String secretkey = "12345678900987654321asdfghjkllkjhgfdsaqwertyuioppoiuytrewq";
    

//    @Value("${jwt.expiration}")
    private long expiration = 60 * 60 * 1000;

    private SecretKey key;

    /**
     * Initializes the secret key after decoding it from Base64.
     */
    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretkey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token JWT token
     * @return Extracted username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token JWT token
     * @return Extracted expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token using a resolver function.
     *
     * @param token JWT token
     * @param claimsResolver Function to resolve the claim
     * @param <T> Type of the claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token JWT token
     * @return Claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the given UserDetails.
     *
     * @param userDetails UserDetails containing username information
     * @return Generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername());
    }

    /**
     * Generates a JWT token for the given User.
     *
     * @param user User entity containing email information
     * @return Generated JWT token
     */
    public String generateToken(User user) {
        return createToken(user.getUsername()); // Adjusted to match User entity field
    }

    /**
     * Creates a JWT token with the subject and expiration time.
     *
     * @param subject Subject (e.g., username)
     * @return Generated JWT token
     */
    private String createToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the given JWT token against the UserDetails.
     *
     * @param token JWT token
     * @param userDetails UserDetails to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Validates the given JWT token against the User entity.
     *
     * @param token JWT token
     * @param user User entity to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token); // Adjusted to match User entity field
    }
}

