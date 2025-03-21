package inventorymanagement.usermodule.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

//    @Value("${jwt.secret}")
    private String secret = "12345678900987654321asdfghjkllkjhgfdsaqwertyuioppoiuytrewq"; // Secret key for signing JWTs

//    @Value("${jwt.expiration}")
    private long expiration = 60 * 60 * 1000; // JWT expiration time in milliseconds

    /**
     * Gets the signing key for JWT encryption.
     *
     * @return SecretKey used for signing the JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extracts the username (subject) from the token.
     *
     * @param token JWT token
     * @return Username as a string
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token.
     *
     * @param token JWT token
     * @param claimsResolver Function to resolve claims
     * @return Resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the token is expired.
     *
     * @param token JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for a given UserDetails object.
     *
     * @param userDetails UserDetails object containing user information
     * @return Generated JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        logger.info("Generating token for username: {}", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with claims, subject, and expiration.
     *
     * @param claims Map of custom claims to be included in the JWT
     * @param subject Subject (typically the username)
     * @return JWT token as a string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the given token with the UserDetails object.
     *
     * @param token JWT token
     * @param userDetails UserDetails object to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        if (isValid) {
            logger.info("Token is valid for username: {}", username);
        } else {
            logger.warn("Token validation failed for username: {}", username);
        }
        return isValid;
    }
}

