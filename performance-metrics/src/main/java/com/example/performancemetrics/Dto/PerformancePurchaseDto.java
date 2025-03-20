package com.example.performancemetrics.Dto;

//package com.example.performancemetrics.Dto;

import java.time.LocalDateTime;

public class PerformancePurchaseDto {
    private String itemName;
    private double price;
    private int quantity;
    private LocalDateTime purchaseDate;

    // Constructors
    public PerformancePurchaseDto() {}

    public PerformancePurchaseDto(String itemName, double price, int quantity, LocalDateTime purchaseDate) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
