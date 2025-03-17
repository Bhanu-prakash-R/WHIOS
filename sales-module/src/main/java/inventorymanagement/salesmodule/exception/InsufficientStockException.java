package inventorymanagement.salesmodule.exception;

//package inventorymanagement.salesmodule.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
