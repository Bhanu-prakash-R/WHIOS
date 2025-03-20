package inventorymanagement.vendormodule.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A generic API response class for standardizing API responses across the application.
 *
 * @param <T> The type of data returned in the response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates whether the API request was successful or not.
     */
    private boolean success;

    /**
     * Provides details about the result of the API request.
     */
    private String message;

    /**
     * Contains the data payload included in the response.
     * Can hold any type of object or collection as specified by the generic parameter.
     */
    private T data;

    /**
     * Represents the HTTP status code of the API response.
     */
    private int status;

    /**
     * Timestamp indicating when the API response was generated.
     */
    private LocalDateTime timestamp;
}
