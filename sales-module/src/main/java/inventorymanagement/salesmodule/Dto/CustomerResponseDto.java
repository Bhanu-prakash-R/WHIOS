package inventorymanagement.salesmodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for representing customer details.
 * Contains fields for customer information and two constructors:
 * - A full constructor to initialize all fields.
 * - A partial constructor for initializing only name and email.
 */
public class CustomerResponseDto {

    private UUID customerId; // Changed from Long to UUID

    private String name;

    private String phoneNumber;

    private String email;

    // Constructor
    public CustomerResponseDto(UUID customerId, String name, String phoneNumber, String email) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

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
}
