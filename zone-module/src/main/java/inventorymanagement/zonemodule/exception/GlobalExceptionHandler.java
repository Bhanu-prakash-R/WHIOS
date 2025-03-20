package inventorymanagement.zonemodule.exception;

import inventorymanagement.zonemodule.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling exceptions across the Zone module.
 * Provides standardized responses for different types of exceptions, improving error reporting and debugging.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ZoneNotFoundException.
     * 
     * @param ex The ZoneNotFoundException object.
     * @return ResponseEntity containing an ApiResponse with error details and an HTTP 404 status code.
     */
    @ExceptionHandler(ZoneNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleZoneNotFoundException(ZoneNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors triggered by @Valid annotations.
     * Extracts field-specific error messages and packages them into a map for the response.
     * 
     * @param ex The MethodArgumentNotValidException object containing validation details.
     * @return ResponseEntity containing an ApiResponse with validation errors and an HTTP 400 status code.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Create a map to store validation errors, where the field name is the key
        // and the error message is the value.
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ApiResponse<Map<String, String>> response = new ApiResponse<>(false, "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any other generic exceptions that are not specifically mapped.
     * Provides a general error message to ensure users are informed of issues.
     * 
     * @param ex The generic Exception object.
     * @return ResponseEntity containing an ApiResponse with error details and an HTTP 500 status code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(false, "An unexpected error occurred: " + ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
