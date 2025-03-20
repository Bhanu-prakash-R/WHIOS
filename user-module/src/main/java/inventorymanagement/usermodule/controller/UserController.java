package inventorymanagement.usermodule.controller;

import inventorymanagement.usermodule.dto.UserDTO;
import inventorymanagement.usermodule.entity.User;
import inventorymanagement.usermodule.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        logger.info("Received request to register user: {}", userDTO.getUsername());
        String response = userService.registerUser(userDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user login.
     *
     * @param userDTO The user credentials.
     * @return A JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        logger.info("Received request to login user: {}", userDTO.getUsername());
        String token = userService.loginUser(userDTO);
        return ResponseEntity.ok(token);
    }
}