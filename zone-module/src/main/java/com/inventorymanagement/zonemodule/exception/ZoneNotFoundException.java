package com.inventorymanagement.zonemodule.exception;

/**
 * Custom exception class for handling cases where a requested zone is not found.
 * Extends the RuntimeException, making it an unchecked exception.
 */
public class ZoneNotFoundException extends RuntimeException {

    /**
     * Constructor for ZoneNotFoundException.
     * 
     * @param message A descriptive message providing details about the exception.
     */
    public ZoneNotFoundException(String message) {
        super(message); // Passes the exception message to the superclass.
    }
}
