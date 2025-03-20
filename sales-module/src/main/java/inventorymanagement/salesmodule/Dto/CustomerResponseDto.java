package inventorymanagement.salesmodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

/**
 * Data Transfer Object (DTO) for representing customer details.
 * Contains fields for customer information and two constructors:
 * - A full constructor to initialize all fields.
 * - A partial constructor for initializing only name and email.
 */
public class CustomerResponseDto {
	/*@NotNull(message = "Customer ID cannot be null")
    @NonNull*/
    private Long customerId;
	
	/*@NotBlank(message = "Name cannot be blank")
    @NonNull*/
    private String name;
	
	/*@NotBlank(message = "Phone number cannot be blank")
	@Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @NonNull*/
	private String phoneNumber;
	
	/*@NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @NonNull*/
    private String email;

    // Constructor
    public CustomerResponseDto( Long customerId, String name, String phoneNumber, String email) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    public CustomerResponseDto( String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     *  Getters and Setters
     */
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId( Long customerId) {
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
