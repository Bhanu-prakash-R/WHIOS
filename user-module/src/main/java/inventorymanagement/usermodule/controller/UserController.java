package inventorymanagement.usermodule.controller;

import inventorymanagement.usermodule.dto.UserDTO;
import inventorymanagement.usermodule.entity.User;
import inventorymanagement.usermodule.response.ApiResponse;
import inventorymanagement.usermodule.service.UserService;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // Validate input
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
                HttpStatus.OK.value(),
                LocalDateTime.now()
               
               
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } 
    }

}