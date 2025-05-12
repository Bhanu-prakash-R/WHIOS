package com.inventorymanagement.usermodule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

    /**
     * JWT secret key used for signing tokens.
     */
    private String secret;

    /**
     * JWT expiration time in milliseconds.
     */
    private long expiration;

    /**
     * Token prefix for Authorization header.
     * Example: "Bearer "
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Header name where the JWT is expected.
     * Example: "Authorization"
     */
    private String header = "Authorization";
}

