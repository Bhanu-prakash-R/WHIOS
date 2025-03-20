package com.example.performancemetrics.Dto;

public class PerformanceItemDto {
    private String itemName;
    private int quantity;

    // Constructors
    public PerformanceItemDto() {}

    public PerformanceItemDto(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    // Getters and setters
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
}
