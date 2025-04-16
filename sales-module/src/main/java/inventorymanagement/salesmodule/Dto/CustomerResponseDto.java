package inventorymanagement.salesmodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for representing customer details.
 * Contains fields for customer information and updated to include the address.
 */
public class CustomerResponseDto {

    private UUID customerId;

    private String name;

    private String phoneNumber;

    private String email;

    private String address; // Added address field

    // Full Constructor
    public CustomerResponseDto(UUID customerId, String name, String phoneNumber, String email, String address) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Partial Constructor
    public CustomerResponseDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     *  Getters and Setters
     */
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
