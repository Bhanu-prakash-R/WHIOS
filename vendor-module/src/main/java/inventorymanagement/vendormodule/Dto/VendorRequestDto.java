package inventorymanagement.vendormodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VendorRequestDto {
	@NotBlank
    private String vendorName;
    private String phoneNumber;
    @Email(message="email should be valid")
    private String email;
    private String address;

    // Getters and Setters
    public String getVendorName() {
        return vendorName;
    }

    public void setName(String vendorName) {
        this.vendorName = vendorName;
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

