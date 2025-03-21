package inventorymanagement.purchasemodule.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
 
/**
* Entity class representing a Purchase.
*/
@Entity
@Data
public class Purchase {
 
    /**
     * Unique identifier for the purchase.
     * Generated automatically using UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID purchaseId;
 
    /**
     * Quantity of items purchased.
     * Cannot be null.
     */
    @Column(nullable = false)
    private int quantity;
 
    /**
     * Price of the purchase.
     * Cannot be null.
     */
    @Column(nullable = false)
    private double price;
 
    /**
     * Date and time when the purchase was made.
     * Cannot be null.
     */
    @Column(nullable = false)
    private LocalDateTime purchaseDate;
 
    /**
     * Identifier for the vendor.
     * Cannot be null.
     */
    @Column(nullable = false)
    private UUID vendorId;
 
    /**
     * Category of the purchased item.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String category;
 
    /**
     * Name of the vendor.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String vendorName;  
 
    /**
     * Name of the purchased item.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String itemName;   
 
    /**
     * Method to set default values before persisting the entity.
     * Generates a UUID for purchaseId if not already set.
     * Sets the purchaseDate to the current date and time.
     */
    @PrePersist
    public void prePersist() {
        if (purchaseId == null) {
            purchaseId = UUID.randomUUID();
        }
        purchaseDate = LocalDateTime.now();
    }
}
 