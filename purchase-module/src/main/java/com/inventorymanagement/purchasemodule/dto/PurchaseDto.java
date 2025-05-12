package com.inventorymanagement.purchasemodule.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
	
	
    private UUID purchaseId;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(  value=1 ,message = "Quantity must be at least 1")
	private int quantity;
    
    private UUID vendorId;
    
    

	private double price;
    
   
	private LocalDateTime purchaseDate;
    
    /*@Min(value = 1, message = "Vendor ID must be a positive integer")
	private int vendorId;*/
    
    @NotBlank(message = "Category cannot be blank")
	private String category;
	
    @NotBlank(message = "Vendor name cannot be blank")
    private String vendorName; 
    
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;   
}