package inventorymanagement.zonemodule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a storage zone within the application.
 * This entity is mapped to the "zones" table.
 *
 * @author Bhanuprakash
 * @since 15 March 2025
 */

@Entity
@Table(name = "zones")
@Data
public class Zone {
	
	/**
     * The unique identifier for the zone.
     */
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID) // Automatically generates a UUID
    private String zoneId; // Changed from Long to UUID

    /**
     * The name of the zone.
     */
    @Column(nullable = false, length = 100)
    private String zoneName;
    
    /**
     * A description of the zone.
     */
    @Column(nullable = false, length = 255)
    private String description;

    /**
     * Indicates whether the zone is active or inactive.
     */
    @Column(nullable = false)
    private Boolean isActive;
    
    /**
     * Generates a UUID for the zoneId field before persisting the entity.
     * This ensures every zone has a unique identifier.
     */
    @PrePersist
    public void generateUuidCreatedAt() {
    	this.zoneId = UUID.randomUUID().toString();
    }
}
