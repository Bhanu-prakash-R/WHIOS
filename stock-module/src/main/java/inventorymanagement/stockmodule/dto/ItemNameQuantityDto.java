package inventorymanagement.stockmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemNameQuantityDto {
    private String itemName;
    private int quantity;
}
