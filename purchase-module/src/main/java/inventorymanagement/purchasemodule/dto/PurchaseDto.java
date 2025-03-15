package inventorymanagement.purchasemodule.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
    private UUID purchaseId;
    private int quantity;
    private double price;
    private LocalDateTime purchaseDate;
    private int vendorId;
    private String category;
    private String vendorName;  
    private String itemName;   
}