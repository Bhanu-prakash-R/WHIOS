package com.inventorymanagement.salesmodule.exception;

//package inventorymanagement.salesmodule.exception;

/**
 * Custom exception for handling cases where there is insufficient stock.
 * Extends RuntimeException to provide a detailed error message when the exception is thrown.
 *
 * @param message The error message describing the insufficient stock issue.
 */

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
