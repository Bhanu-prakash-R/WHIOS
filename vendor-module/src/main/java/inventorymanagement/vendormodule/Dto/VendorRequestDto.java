package inventorymanagement.vendormodule.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for vendor request payloads.
 * This class is used to validate and encapsulate vendor-related information 
 * that is sent in API requests.
 */
public class VendorRequestDto {

    /**
     * The name of the vendor.
     * Must not be blank and must be between 2 and 100 characters.
     */
    @NotBlank(message = "Vendor name cannot be blank")
    @Size(min = 2, max = 100, message = "Vendor name must be between 2 and 100 characters")
    private String vendorName;

    /**
     * The phone number of the vendor.
     * Must not be blank and must be between 10 and 15 characters.
     */
    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

    /**
     * The email address of the vendor.
     * Must be a valid email format and cannot be blank.
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    /**
     * The physical address of the vendor.
     * Must not be blank and must not exceed 255 characters.
     */
    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * Retrieves the vendor's name.
     *
     * @return The vendor's name.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendor's name.
     *
     * @param vendorName The name to set for the vendor.
     */
    public void setName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Retrieves the vendor's phone number.
     *
     * @return The vendor's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the vendor's phone number.
     *
     * @param phoneNumber The phone number to set for the vendor.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the vendor's email address.
     *
     * @return The vendor's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the vendor's email address.
     *
     * @param email The email address to set for the vendor.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the vendor's address.
     *
     * @return The vendor's physical address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the vendor's address.
     *
     * @param address The address to set for the vendor.
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
