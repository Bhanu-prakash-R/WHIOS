package inventorymanagement.salesmodule.exception;

public class InvalidSaleRequestException extends RuntimeException {
    public InvalidSaleRequestException(String message) {
        super(message);
    }
}
