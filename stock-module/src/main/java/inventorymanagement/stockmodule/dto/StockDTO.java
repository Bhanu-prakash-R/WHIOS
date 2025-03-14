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

@Data
@NoArgsConstructor
public class StockDTO {
    private UUID stockId;

    @NotBlank(message = "Item name cannot be blank")
    private String itemName;

    @Min(value = 0, message = "Quantity must be at least 0")
    private int quantity;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Positive(message = "Price must be positive")
    private double price;

    @NotBlank(message = "Zone name cannot be blank")
    private String zoneName;

    @NotBlank(message = "Vendor name cannot be blank")
    private String vendorName;

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
}
