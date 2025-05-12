package com.inventorymanagement.stockmodule.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailsDto {
    private String vendorName;
    private int quantity;
    private double price;
    private String category;
   private LocalDateTime purchaseDate;
   
}
