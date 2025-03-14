package inventorymanagement.vendormodule.Dto;

import jakarta.validation.constraints.Email;

public class ContactDetailsDto {
    private String phoneNumber;
    @Email(message="email should be valid")
    private String email;
    private String address;

    // Constructor, Getters, and Setters
    public ContactDetailsDto(String phoneNumber, String email, String address) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
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
