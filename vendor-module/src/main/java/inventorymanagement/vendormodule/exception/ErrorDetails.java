package inventorymanagement.vendormodule.exception;

/**
 * A simple POJO (Plain Old Java Object) class for encapsulating error details.
 * This class is typically used for returning structured error messages
 * in API responses when exceptions occur.
 */
public class ErrorDetails {

    /**
     * A descriptive message about the error.
     * Provides an overview of the issue that caused the exception.
     */
    private String message;

    /**
     * Additional details about the error.
     * Can include information like request context or specific failure reasons.
     */
    private String details;

    /**
     * Constructor to initialize the error details.
     *
     * @param message A descriptive message about the error.
     * @param details Additional information about the error.
     */
    public ErrorDetails(String message, String details) {
        this.message = message;
        this.details = details;
    }

    /**
     * Retrieves the error message.
     *
     * @return A {@code String} containing the error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message.
     *
     * @param message The error message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves additional error details.
     *
     * @return A {@code String} containing additional error details.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets additional error details.
     *
     * @param details The additional error details to set.
     */
    public void setDetails(String details) {
        this.details = details;
    }
}
