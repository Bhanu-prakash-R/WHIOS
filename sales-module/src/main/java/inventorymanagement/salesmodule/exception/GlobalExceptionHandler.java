package inventorymanagement.salesmodule.exception;

import inventorymanagement.salesmodule.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Global exception handler for handling application-wide errors.
 * - Maps specific exceptions (e.g., SaleNotFoundException, CustomerNotFoundException) to appropriate HTTP status codes and structured API responses.
 * - Provides a global fallback for unhandled exceptions and a specific handler for insufficient stock scenarios.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles SaleNotFoundException and returns a structured API response.
     *
     * @param ex      The exception thrown when a sale is not found.
     * @param request The current web request.
     * @return ApiResponse with error details and a NOT_FOUND status.
     */
    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleSaleNotFoundException(SaleNotFoundException ex, WebRequest request) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            ex.getMessage(),
            null,
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles CustomerNotFoundException and returns a structured API response.
     *
     * @param ex      The exception thrown when a customer is not found.
     * @param request The current web request.
     * @return ApiResponse with error details and a NOT_FOUND status.
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomerNotFoundException(CustomerNotFoundException ex, WebRequest request) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            ex.getMessage(),
            null,
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles InvalidSaleRequestException and returns a structured API response.
     *
     * @param ex      The exception thrown for invalid sale requests.
     * @param request The current web request.
     * @return ApiResponse with error details and a BAD_REQUEST status.
     */
    @ExceptionHandler(InvalidSaleRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidSaleRequestException(InvalidSaleRequestException ex, WebRequest request) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            ex.getMessage(),
            null,
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles InsufficientStockException and returns a structured API response.
     *
     * @param ex The exception thrown when stock is insufficient for a sale.
     * @return ApiResponse with error details and a BAD_REQUEST status.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Object>> handleInsufficientStockException(InsufficientStockException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            ex.getMessage(),
            null,
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles all unhandled exceptions and returns a structured API response.
     *
     * @param ex      The unhandled exception.
     * @param request The current web request.
     * @return ApiResponse with error details and an INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponse<Object> response = new ApiResponse<>(
            false,
            "An unexpected error occurred: " + ex.getMessage(),
            null,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
