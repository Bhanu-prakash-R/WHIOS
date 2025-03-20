package inventorymanagement.stockmodule.entity;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entity class for mapping to the "stock_management" table in the database.
 * Uses Lombok to reduce boilerplate with @Data for getters and setters 
 * and @NoArgsConstructor for a no-argument constructor.
 */
@Entity
@Data
@Table(name="stock_management")
@NoArgsConstructor
/**
 * Entity class representing stocks in the "stock_management" table.
 * The stockId is a unique identifier for each stock, mapped to the "stock_id" column.
 */
public class Stocks {
    @Id
    @Column(name="stock_id")
    private UUID stockId;

    /**
     * Represents stock details with database mappings and constraints.
     * Fields include item name, quantity, category, price, zone, and vendor details.
     * Ensures data integrity with annotations like @Column for nullability and uniqueness.
     */
    
    @Column(name = "name", nullable = false, unique = true)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private double price;

    private long zoneId;

    @Column(nullable = false)
    private String zoneName;

    private long vendorId;

    @Column(nullable = false)
    private String vendorName;

    /**
     * Tracks the creation and update timestamps for the stock record.
     * - @CreationTimestamp: Automatically sets the creation time and is non-updatable.
     * - @UpdateTimestamp: Automatically updates the timestamp on any modification.
     *
     * Also includes a purchase ID field for associating stock with a specific purchase.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private long purchaseId;

    /*@OneToMany(mappedBy = "stocks", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Notification> notifications = new HashSet<>();*/

    public Stocks(UUID stockId, String itemName, int quantity, String category, double price, long zoneId, String zoneName, long vendorId, String vendorName, long purchaseId) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.purchaseId = purchaseId;
    }
}

