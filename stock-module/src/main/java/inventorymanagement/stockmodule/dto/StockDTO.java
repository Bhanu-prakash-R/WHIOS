package inventorymanagement.stockmodule.dto;

import java.time.LocalDateTime;


import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
/**
 * Lombok annotations to reduce boilerplate code.
 *
 * - @Data: Generates getters, setters, toString, equals, and hashCode methods.
 * - @AllArgsConstructor: Generates a constructor with arguments for all fields.
 */
@Data
@NoArgsConstructor
/**
 * DTO for stock details with validation.
 */
public class StockDTO {
    private UUID stockId;
    /**
     * Includes validation to ensure itemName is not null or blank.
     */
    @NonNull
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;
    
    /**
     * Quantity of the stock item. 
     * Must not be negative and is validated to ensure it's at least 0.
     */
    @NonNull
    @Min(value = 0, message = "Quantity must be at least 0")
    private int quantity;

    /**
     * Category of the stock item. 
     * Must not be null or blank, ensuring meaningful input.
     */
    @NonNull
    @NotBlank(message = "Category cannot be blank")
    private String category;

    /**
     * Price of the stock item. 
     * Must be positive and cannot be null to ensure valid pricing.
     */
    @NonNull
    @Positive(message = "Price must be positive")
    private double price;

    /**
     * Zone name where the stock is located.
     * Must not be null or blank to ensure accurate location information.
     */
    @NonNull
    @NotBlank(message = "Zone name cannot be blank")
    private String zoneName;

    /**
     * Vendor name associated with the stock item.
     * Must not be null or blank to ensure vendor identification.
     */
    @NonNull
    @NotBlank(message = "Vendor name cannot be blank")
    private String vendorName;

    /**
     * DTO for representing stock details with validation constraints.
     * Contains fields like stock ID, item name, quantity, category, price, 
     * zone name, vendor name, notifications, and creation timestamp.
     * Ensures data integrity with validation annotations such as @NotBlank and @Positive.
     * Includes constructors for full and partial initialization.
     */
    private List<String> notificationMessages;

    private LocalDateTime createdAt;

    public StockDTO(UUID stockId, String itemName, int quantity, String category, double price, String zoneName, String vendorName, List<String> notificationMessages, LocalDateTime createdAt) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.zoneName = zoneName;
        this.vendorName = vendorName;
        this.notificationMessages = notificationMessages;
        this.createdAt = createdAt;
    }
    public StockDTO(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
}
