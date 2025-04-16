package inventorymanagement.stockmodule.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for stock details with validation.
 * Fields are validated to ensure data integrity.
 */
@Data
@NoArgsConstructor
public class StockDTO {

    private UUID stockId;

    /**
     * Includes validation to ensure itemName is not null or blank.
     * This field is mandatory and must be provided by the client.
     */
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;

    /**
     * Quantity of the stock item.
     * Optional: Can be autofilled from the Purchase Module if missing or zero.
     */
    @Min(value = 0, message = "Quantity must be at least 0")
    private int quantity;

    /**
     * Category of the stock item.
     * Optional: Can be autofilled from the Purchase Module if missing.
     */
    private String category;

    /**
     * Price of the stock item.
     * Optional: Must be positive if provided but can be autofilled if missing.
     */
    @Min(value = 0, message = "Price must be at least 0")
    private double price;

    /**
     * Zone name where the stock is located.
     * Mandatory for accurate location information.
     */
    @NotBlank(message = "Zone name cannot be blank")
    private String zoneName;

    /**
     * Vendor name associated with the stock item.
     * Optional: Can be autofilled from the Purchase Module if missing.
     */
    private String vendorName;

    /**
     * Notification messages associated with the stock item.
     */
    private List<String> notificationMessages;

    /**
     * Timestamp when the stock entry was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp for when the stock was last restocked.
     * This is crucial for determining if a new purchase is available for restocking.
     */
    private LocalDateTime lastRestockedAt;

    // Full constructor
    public StockDTO(UUID stockId, String itemName, int quantity, String category, double price, String zoneName, String vendorName, List<String> notificationMessages, LocalDateTime createdAt, LocalDateTime lastRestockedAt) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.zoneName = zoneName;
        this.vendorName = vendorName;
        this.notificationMessages = notificationMessages;
        this.createdAt = createdAt;
        this.lastRestockedAt = lastRestockedAt;
    }

    // Constructor for itemName and quantity only
    public StockDTO(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
}
