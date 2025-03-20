package inventorymanagement.salesmodule.Dto;

import java.time.LocalDateTime;

import inventorymanagement.salesmodule.Dto.CustomerResponseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
/**
 * Data Transfer Object (DTO) for representing sale details.
 * Contains fields for sale information such as sale ID, item name, quantity, price, sale date, and customer details (via a nested DTO).
 * Provides two constructors:
 * - Full constructor to initialize all fields, including customer details.
 * - Partial constructor for initializing only sale ID and item name.
 */
public class SaleResponseDto {
	/*@NotNull(message = "Sale ID cannot be null")
	@NonNull*/
    private Long saleId;
	
	/*@NotBlank(message = "Item name cannot be blank")
    @NonNull*/
    private String itemName;
	
	/*@NotNull(message = "Quantity cannot be null")
    @NonNull
	@Min(value = 0, message = "Quantity must be at least 0")*/
    private int quantity;
	
	/*@NotNull(message = "Price cannot be null")
    @NonNull
	@Min(value = 0, message = "Price must be a non-negative value")*/
    private double price;
	
	 /*@NotNull(message = "Sale date cannot be null")
	 @NonNull*/
    private LocalDateTime saleDate;
	 
	 /*@NonNull
	 @NotNull(message = "Customer details cannot be null")*/
    private CustomerResponseDto customer; // Nested DTO for customer details

    // Constructor
    public SaleResponseDto( Long saleId, String itemName, int quantity, double price, LocalDateTime saleDate, CustomerResponseDto customer) {
        this.saleId = saleId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
        this.customer = customer;
    }
    public SaleResponseDto( Long saleId, String itemName) {
        this.saleId = saleId;
        this.itemName = itemName;
    }


    /** Getters and Setters
     * 
     * @return
     */
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public CustomerResponseDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponseDto customer) {
        this.customer = customer;
    }
}
