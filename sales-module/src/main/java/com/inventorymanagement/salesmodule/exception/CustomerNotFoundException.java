package com.inventorymanagement.salesmodule.exception;

/**
 * Deletes a sale record by its ID.
 * Logs the operation, invokes the service to delete the sale, and returns no content upon success.
 * Handles exceptions for missing sales or unexpected errors.
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
