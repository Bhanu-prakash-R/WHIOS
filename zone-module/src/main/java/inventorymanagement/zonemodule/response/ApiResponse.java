package inventorymanagement.zonemodule.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic class for standardizing API responses across the application.
 * Utilizes generics to support responses with various types of data payloads.
 * Includes success status, a descriptive message, and the actual response data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates whether the API call was successful.
     * True for successful operations, false otherwise.
     */
    private boolean success;

    /**
     * A descriptive message providing details about the result of the API call.
     * Used to convey success, failure, or error-related information to the client.
     */
    private String message;

    /**
     * The actual data payload returned by the API.
     * Can be any type, as specified by the generic parameter {@code T}.
     */
    private T data;
}
