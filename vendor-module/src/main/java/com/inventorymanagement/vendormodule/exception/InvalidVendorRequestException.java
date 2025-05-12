package com.inventorymanagement.vendormodule.exception;

/**
 * Custom exception class for handling invalid vendor request scenarios.
 * Extends {@code RuntimeException}, making it an unchecked exception.
 * This exception is thrown when a vendor request does not meet the expected requirements or constraints.
 */
public class InvalidVendorRequestException extends RuntimeException {

    /**
     * Constructor for the InvalidVendorRequestException class.
     * Accepts a descriptive error message to provide more context about the exception.
     *
     * @param message A detailed message explaining the reason for the exception.
     */
    public InvalidVendorRequestException(String message) {
        super(message); // Passes the error message to the RuntimeException superclass
    }
}
