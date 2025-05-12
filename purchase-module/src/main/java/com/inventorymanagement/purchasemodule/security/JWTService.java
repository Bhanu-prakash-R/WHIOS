package com.inventorymanagement.purchasemodule.security;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
 
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
 
@Service
public class JWTService {
 
    private static final String secretkey = "12345678900987654321asdfghjkllkjhgfdsaqwertyuioppoiuytrewq";
 
    private static SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
 
    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
 
    public String extractRoles(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
 
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
 
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
 
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            System.out.println("validate token:"+token);
            
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            return false;
        }
    }
}