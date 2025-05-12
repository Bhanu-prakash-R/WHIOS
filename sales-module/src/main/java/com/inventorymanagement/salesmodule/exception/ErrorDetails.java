package com.inventorymanagement.salesmodule.exception;

/**
 * Represents error details for exception handling.
 * Contains fields for an error message and additional details.
 * Provides constructors, getters, and setters for data access and modification.
 */
public class ErrorDetails {
    private String message;
    private String details;

    public ErrorDetails(String message, String details) {
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
