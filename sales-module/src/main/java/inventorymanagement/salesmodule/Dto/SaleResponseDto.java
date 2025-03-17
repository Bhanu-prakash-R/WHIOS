package inventorymanagement.salesmodule.Dto;

import java.time.LocalDateTime;

import inventorymanagement.salesmodule.Dto.CustomerResponseDto;

public class SaleResponseDto {

    private Long saleId;
    private String itemName;
    private int quantity;
    private double price;
    private LocalDateTime saleDate;
    private CustomerResponseDto customer; // Nested DTO for customer details

    // Constructor
    public SaleResponseDto(Long saleId, String itemName, int quantity, double price, LocalDateTime saleDate, CustomerResponseDto customer) {
        this.saleId = saleId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
        this.customer = customer;
    }
    public SaleResponseDto(Long saleId, String itemName) {
        this.saleId = saleId;
        this.itemName = itemName;
    }


    // Getters and Setters
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

    public CustomerResponseDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponseDto customer) {
        this.customer = customer;
    }
}
