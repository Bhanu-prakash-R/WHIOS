package com.inventorymanagement.usermodule.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventorymanagement.usermodule.dto.UserDTO;
import com.inventorymanagement.usermodule.exception.UserAlreadyPresentException; // Import this
import com.inventorymanagement.usermodule.response.ApiResponse;
import com.inventorymanagement.usermodule.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration.
     *
     * @param userDTO The user details.
     * @return A success message.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody UserDTO userDTO) {
        logger.info("Received request to register user: {}", userDTO.getUsername());

        try {
            // Validate input (you can use @Valid and @BindingResult for more comprehensive validation)
            if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
                logger.error("Registration failed: username is null or blank.");
                throw new IllegalArgumentException("Username cannot be null or blank.");
            }
            if (userDTO.getPasswordHash() == null || userDTO.getPasswordHash().isBlank()) {
                logger.error("Registration failed: password is null or blank.");
                throw new IllegalArgumentException("Password cannot be null or blank.");
            }

            // Perform registration
            String responseMessage = userService.registerUser(userDTO);
            logger.info("User '{}' registered successfully.", userDTO.getUsername());

            // Success response
            ApiResponse<Void> response = new ApiResponse<>(
                true,
                responseMessage,
                null,
                HttpStatus.OK.value(),
                LocalDateTime.now()
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            logger.error("Error during user registration: {}", ex.getMessage());
            ApiResponse<Void> errorResponse = new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (UserAlreadyPresentException ex) {
            logger.error("Registration failed: {}", ex.getMessage());
            ApiResponse<Void> errorResponse = new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                HttpStatus.CONFLICT.value(), // Use 409 Conflict for existing user
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception ex) {
            logger.error("Unexpected error during user registration: {}", ex.getMessage(), ex);
            ApiResponse<Void> errorResponse = new ApiResponse<>(
                false,
                "Registration failed due to an unexpected error.",
                null,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * Endpoint for user login.
     *
     * @param userDTO The user credentials.
     * @return A JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserDTO userDTO) {
        logger.info("Received request to login user: {}", userDTO.getUsername());

        try {
            // Validate input
            if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
                logger.error("Login failed: username is null or blank.");
                throw new IllegalArgumentException("Username cannot be null or blank.");
            }
            if (userDTO.getPasswordHash() == null || userDTO.getPasswordHash().isBlank()) {
                logger.error("Login failed: password is null or blank.");
                throw new IllegalArgumentException("Password cannot be null or blank.");
            }

            // Perform login and generate JWT token
            String token = userService.loginUser(userDTO);
            logger.info("User '{}' logged in successfully. Token generated.", userDTO.getUsername());

            // Success response with token
            ApiResponse<String> response = new ApiResponse<>(
                true,
                "Login successful",
                token,
                HttpStatus.OK.value(),
                LocalDateTime.now()
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            logger.error("Error during user login: {}", ex.getMessage());
            ApiResponse<String> errorResponse = new ApiResponse<>(
                false,
                ex.getMessage(),
                null,
                HttpStatus.BAD_REQUEST.value(), // Use 400 for invalid input
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception ex) {
            logger.error("Login failed: {}", ex.getMessage());
            ApiResponse<String> errorResponse = new ApiResponse<>(
                false,
                "Login failed: Invalid credentials or account issue.", // More generic message for security
                null,
                HttpStatus.UNAUTHORIZED.value(), // Use 401 Unauthorized for login failures
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}