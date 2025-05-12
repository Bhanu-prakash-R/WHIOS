package com.inventorymanagement.salesmodule.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A generic API response class for standardizing responses.
 *
 * @param <T> The type of data returned in the response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;         // Indicates whether the request was successful
    private String message;          // Detailed message about the response
    private T data;                  // The payload or response data
    private int status;              // HTTP status code
    private LocalDateTime timestamp; // Timestamp of the response
}
