package com.inventorymanagement.zonemodule.exception;

public class ZoneAlreadyExistsException extends RuntimeException {

    public ZoneAlreadyExistsException(String message) {
        super(message);
    }

    public ZoneAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
