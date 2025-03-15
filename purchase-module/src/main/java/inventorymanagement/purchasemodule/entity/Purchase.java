package inventorymanagement.purchasemodule.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID purchaseId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private int vendorId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String vendorName;  

    @Column(nullable = false)
    private String itemName;   

    @PrePersist
    public void prePersist() {
        if (purchaseId == null) {
            purchaseId = UUID.randomUUID();
        }
        purchaseDate = LocalDateTime.now();
    }
}
