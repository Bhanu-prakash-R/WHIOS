package inventorymanagement.salesmodule.model;

import java.time.LocalDateTime;
//import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
/**
 * Represents a Sale entity mapped to the "Sales" table.
 * Contains fields for sale details (saleId, itemName, quantity, price, timestamps) 
 * and a Many-to-One relationship with the Customer entity.
 * Leverages JPA annotations for database mapping and automatic timestamp handling.
 */
@Entity
@Table(name = "Sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id", nullable = false, updatable = false)
    private Long saleId;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;
    

    @Column(nullable = false)
    private double price;
    
    

	@Column
    private  Long StockId;

    @Column(name = "sale_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime saleDate;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "last_updated", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    /**
     *  Getters and setters
    
     */
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Long getStockId() {
		return StockId;
	}

	public void setStockId(Long stockId) {
		StockId = stockId;
	}
}
