package com.inventorymanagement.purchasemodule.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
 
import java.util.HashMap;
import java.util.Map;
 
/**
* Global exception handler for handling validation exceptions.
*/
@ControllerAdvice
public class GlobalExceptionHandler {
 
    /**
     * Handles MethodArgumentNotValidException and returns a response entity with validation errors.
     *
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity containing a map of field errors and HTTP status BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
 