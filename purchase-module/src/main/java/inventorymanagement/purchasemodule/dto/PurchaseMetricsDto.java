package inventorymanagement.purchasemodule.dto;

//package inventorymanagement.purchasemodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseMetricsDto {
    private int quantity;
    private double price;
    private LocalDateTime purchaseDate;
    private String itemName;
}