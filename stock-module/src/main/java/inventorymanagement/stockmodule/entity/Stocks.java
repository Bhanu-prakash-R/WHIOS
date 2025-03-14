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

@Entity
@Data
@Table(name = "stock_management")
@NoArgsConstructor
public class Stocks {
    @Id
    @Column(name="stock_id")
    private UUID stockId;

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

