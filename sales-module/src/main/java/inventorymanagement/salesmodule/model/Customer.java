package inventorymanagement.salesmodule.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entity class representing a Customer in the system.
 * Maps to a database table for storing customer-related information.
 * Includes embedded contact details and a one-to-many relationship with sales records.
 */
@Entity
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Automatically generates UUIDs
    private UUID customerId; // Changed from Long to UUID
    
    private String name;

    @Embedded
    private ContactDetails contactDetails;

    @OneToMany(mappedBy = "customer")
    private List<Sales> sales;

    /**
     * Getters and Setters
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

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public List<Sales> getSales() {
        return sales;
    }

    public void setSales(List<Sales> sales) {
        this.sales = sales;
    }
}
