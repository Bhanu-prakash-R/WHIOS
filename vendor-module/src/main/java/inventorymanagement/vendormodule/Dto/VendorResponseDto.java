package inventorymanagement.vendormodule.Dto;

import java.util.UUID;

import inventorymanagement.vendormodule.Dto.ContactDetailsDto;

public class VendorResponseDto {
    private UUID vendorId;
    private String vendorName;
    private ContactDetailsDto contactDetails;

    // Constructor, Getters, and Setters
    public VendorResponseDto(UUID vendorId, String vendorName, ContactDetailsDto contactDetails) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.contactDetails = contactDetails;
    }

    public UUID getVendorId() {
        return vendorId;
    }

    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setName(String vendorName) {
        this.vendorName = vendorName;
    }

    public ContactDetailsDto getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetailsDto contactDetails) {
        this.contactDetails = contactDetails;
    }
}


