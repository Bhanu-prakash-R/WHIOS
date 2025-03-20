package inventorymanagement.stockmodule.exception;
/**
 * Custom exception for handling scenarios where a stock item is not found.
 * Extends RuntimeException to represent an unchecked exception.
 */
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {
        super(message);
    }
} 

