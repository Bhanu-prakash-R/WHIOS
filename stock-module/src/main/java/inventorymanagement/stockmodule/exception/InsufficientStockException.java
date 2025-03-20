package inventorymanagement.stockmodule.exception;

/**
 * Custom exception for handling insufficient stock scenarios.
 * Extends RuntimeException to indicate an unchecked exception.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
