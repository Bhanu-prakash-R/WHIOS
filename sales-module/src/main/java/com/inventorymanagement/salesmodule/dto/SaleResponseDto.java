package com.inventorymanagement.salesmodule.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.inventorymanagement.salesmodule.dto.CustomerResponseDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for representing sale details.
 * Contains fields for sale information such as sale ID, item name, quantity, price, sale date, and customer details (via a nested DTO).
 * Provides two constructors:
 * - Full constructor to initialize all fields, including customer details.
 * - Partial constructor for initializing only sale ID and item name.
 */
public class SaleResponseDto {

    private UUID saleId; // Changed from Long to UUID

    private String itemName;

    private int quantity;

    private double price;

    private LocalDateTime saleDate;

    private CustomerResponseDto customer; // Nested DTO for customer details

    // Constructor
    public SaleResponseDto(UUID saleId, String itemName, int quantity, double price, LocalDateTime saleDate, CustomerResponseDto customer) {
        this.saleId = saleId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
        this.customer = customer;
    }

    public SaleResponseDto(UUID saleId, String itemName) {
        this.saleId = saleId;
        this.itemName = itemName;
    }

    /**
     *  Getters and Setters
     */
    public UUID getSaleId() {
        return saleId;
    }

    public void setSaleId(UUID saleId) {
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
