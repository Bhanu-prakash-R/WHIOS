package inventorymanagement.purchasemodule.dto;

import java.util.UUID;

public class VendorResponseDto {
    private UUID vendorId; // Unique identifier for the vendor
    private String vendorName; // Name of the vendor

    // Constructor
    public VendorResponseDto(UUID vendorId, String vendorName) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    // Getters and Setters
    public UUID getVendorId() {
        return vendorId;
    }

    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
