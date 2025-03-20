package inventorymanagement.vendormodule.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import inventorymanagement.vendormodule.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle VendorNotFoundException.
     *
     * @param ex The exception thrown.
     * @return ApiResponse containing error details.
     */
    @ExceptionHandler(VendorNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleVendorNotFoundException(VendorNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(
            false, 
            ex.getMessage(), 
            null, 
            404, 
            LocalDateTime.now()
        );
        return ResponseEntity.status(404).body(response);
    }

    /**
     * Handle InvalidVendorRequestException.
     *
     * @param ex The exception thrown.
     * @return ApiResponse containing error details.
     */
    @ExceptionHandler(InvalidVendorRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidVendorRequestException(InvalidVendorRequestException ex) {
        ApiResponse<String> response = new ApiResponse<>(
            false, 
            ex.getMessage(), 
            null, 
            400, 
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle generic exceptions.
     *
     * @param ex The exception thrown.
     * @return ApiResponse containing error details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>(
            false, 
            "An unexpected error occurred: " + ex.getMessage(), 
            null, 
            500, 
            LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(response);
    }
}
