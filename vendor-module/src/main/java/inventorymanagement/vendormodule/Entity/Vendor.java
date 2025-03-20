package inventorymanagement.vendormodule.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

/**
 * Entity class for the Vendor table.
 * Represents a vendor in the system, including its unique identifier, name, 
 * and associated contact details.
 */
@Entity
public class Vendor {

    /**
     * Unique identifier for the vendor.
     * Automatically generated using UUID.
     * This field is mapped to the primary key in the database.
     */
    @Id
    private UUID vendorId = UUID.randomUUID();

    /**
     * The name of the vendor.
     * This field is mandatory and cannot be null.
     */
    @Column(nullable = false)
    private String vendorName;

    /**
     * Embedded field for contact details.
     * Represents a set of related fields (e.g., phone number, email, address)
     * stored within this entity.
     */
    @Embedded
    private ContactDetails contactDetails;

    /**
     * Retrieves the unique identifier of the vendor.
     *
     * @return The vendor's UUID.
     */
    public UUID getVendorId() {
        return vendorId;
    }

    /**
     * Sets the unique identifier for the vendor.
     *
     * @param vendorId The UUID to assign to the vendor.
     */
    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * Retrieves the name of the vendor.
     *
     * @return The vendor's name.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the name of the vendor.
     *
     * @param vendorName The name to assign to the vendor.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Retrieves the contact details of the vendor.
     *
     * @return The embedded ContactDetails object.
     */
    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the contact details for the vendor.
     *
     * @param contactDetails The ContactDetails object to assign.
     */
    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }
}
