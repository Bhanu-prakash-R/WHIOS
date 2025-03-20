package inventorymanagement.salesmodule.exception;

/**
 * Custom exception for handling scenarios where a sale record is not found.
 * Extends RuntimeException to provide a meaningful error message when triggered.
 *
 * @param message The detailed error message explaining the missing sale record.
 */

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(String message) {
        super(message);
    }
}
