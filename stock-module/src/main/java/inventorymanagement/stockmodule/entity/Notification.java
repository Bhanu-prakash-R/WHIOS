package inventorymanagement.stockmodule.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing the notifications table in the database.
 * Uses Lombok annotations for boilerplate reduction such as getters, setters, and constructors.
 * Annotated with @Entity for JPA mapping and @Table to specify the database table name.
 * @NoArgsConstructor ensures a no-argument constructor is generated.
 */
@Entity
@Data
@Table(name = "notifications", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"stock_id", "message"})
})
@NoArgsConstructor

/**
 * Entity class representing a notification.
 * Maps to a database table with a unique identifier.
 * The ID is auto-generated using GenerationType.AUTO strategy.
 */
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Many-to-one relationship between notifications and stocks.
     * Maps the 'stocks' field to the 'stock_id' column in the database.
     * Ensures the 'stock_id' column is non-null to maintain data integrity.
     */

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stocks stocks;

    /**
     * Represents the message content of the notification.
     * Maps to a non-nullable column in the database to ensure every notification has a message.
     */
    @Column(nullable = false)
    private String message;

    /**
     * Automatically captures the timestamp when the entity is created.
     * Maps to a database column storing the creation time.
     * Uses @CreationTimestamp to ensure the value is set only during insertion.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Indicates whether the notification has been read.
     * Maps to a non-nullable column in the database with a default value of false.
     */
    @Column(nullable = false)
    private boolean isRead = false;

    /**
     * Constructor to initialize a notification with the associated stock and message.
     */
    public Notification(Stocks stocks, String message) {
        this.stocks = stocks;
        this.message = message;
    }
}

