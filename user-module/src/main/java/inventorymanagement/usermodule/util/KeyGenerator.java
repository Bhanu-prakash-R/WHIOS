package inventorymanagement.usermodule.util;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Utility class for generating a secure Base64-encoded key for JWT signing.
 */
public class KeyGenerator {
    public static void main(String[] args) {
        // Generate a secure key using the HS256 algorithm
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Encode the generated key into Base64 format
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        // Output the Base64-encoded key
        System.out.println("Generated JWT Secret Key (Base64-Encoded): " + encodedKey);
    }
}

