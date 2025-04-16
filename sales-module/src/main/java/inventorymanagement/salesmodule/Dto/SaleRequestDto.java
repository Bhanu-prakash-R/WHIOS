package inventorymanagement.salesmodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) for handling sale creation requests.
 * Includes fields for customer details (name, phone, email, address) and sale-specific information
 * (customer ID, item name, quantity, price).
 */
public class SaleRequestDto {

    private UUID customerId; // Optional field for existing customers

    @NotBlank(message = "Customer name cannot be blank")
    @NonNull
    private String customerName;

    @NotBlank(message = "Customer phone cannot be blank")
    @NonNull
    @Size(min = 10, max = 15, message = "Customer phone must be between 10 and 15 characters")
    private String customerPhone;

    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Customer email should be valid")
    @NonNull
    private String customerEmail;

    @NotBlank(message = "Customer address cannot be blank")
    @NonNull
    @Size(max = 255, message = "Customer address must not exceed 255 characters")
    private String customerAddress;

    @NotBlank(message = "Item name cannot be blank")
    @NonNull
    private String itemName;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @NonNull
    @Positive(message="quanity must be positive")
    private Integer quantity;

    @NotNull
    @Positive(message = "Price must be positive")
    @NonNull
    private double price;

    /**
     * Getters and Setters
     */
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
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
}
