package com.inventorymanagement.usermodule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventorymanagement.usermodule.dao.UserRepository;
import com.inventorymanagement.usermodule.dto.UserDTO;
import com.inventorymanagement.usermodule.entity.User;
import com.inventorymanagement.usermodule.exception.UserAlreadyPresentException;
import com.inventorymanagement.usermodule.util.JwtUtil;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO The user details encapsulated in a UserDTO.
     * @return A success message.
     */
    public String registerUser(UserDTO userDTO) {
        logger.info("Attempting to register user: {}", userDTO.getUsername());

        // Check for null or empty fields
        if (userDTO.getUsername() == null || userDTO.getPasswordHash() == null) {
            logger.error("Registration failed: username or password is null");
            throw new IllegalArgumentException("Username and password must not be null");
        }

        // Check if a user with this username already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            logger.warn("User with username '{}' already exists.", userDTO.getUsername());
            throw new UserAlreadyPresentException("User with this username already exists.");
        }

        // Encrypt the password and save the user
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));

        userRepository.save(user);
        logger.info("User registered successfully: {}", userDTO.getUsername());
        return "User registered successfully";
    }

    /**
     * Logs in a user and returns a JWT token.
     *
     * @param userDTO The user credentials encapsulated in a UserDTO.
     * @return A JWT token.
     */
    public String loginUser(UserDTO userDTO) {
        logger.info("Attempting login for user: {}", userDTO.getUsername());

        // Check for null or empty fields
        if (userDTO.getUsername() == null || userDTO.getPasswordHash() == null) {
            logger.error("Login failed: username or password is null");
            throw new IllegalArgumentException("Username and password must not be null");
        }

        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> {
                    logger.error("Login failed: user not found with username {}", userDTO.getUsername());
                    return new RuntimeException("Invalid username or password");
                });

        // Check if the password matches
        if (!passwordEncoder.matches(userDTO.getPasswordHash(), user.getPasswordHash())) {
            logger.error("Login failed: invalid password for username {}", userDTO.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        logger.info("Login successful for user: {}", userDTO.getUsername());
        return token;
    }
}