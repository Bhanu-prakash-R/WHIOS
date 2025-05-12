package com.inventorymanagement.salesmodule.exception;
/**
 * Custom exception for handling invalid sale requests.
 * Extends RuntimeException to provide a detailed error message when triggered.
 *
 * @param message The error message describing the invalid sale request.
 */
public class InvalidSaleRequestException extends RuntimeException {
    public InvalidSaleRequestException(String message) {
        super(message);
    }
}
