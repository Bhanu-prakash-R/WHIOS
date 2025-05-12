package com.inventorymanagement.vendormodule.exception;

/**
 * Custom exception class for handling scenarios where a vendor is not found.
 * Extends {@code RuntimeException}, making it an unchecked exception.
 * This exception is typically thrown when a requested vendor cannot be found in the system.
 */
public class VendorNotFoundException extends RuntimeException {

    /**
     * Constructor for the VendorNotFoundException class.
     * Accepts a descriptive error message to provide context about the exception.
     *
     * @param message A detailed message explaining the reason for the exception.
     */
    public VendorNotFoundException(String message) {
        super(message); // Passes the error message to the RuntimeException superclass
    }
}
