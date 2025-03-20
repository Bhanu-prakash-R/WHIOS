package com.example.performancemetrics.Dto;

//package inventorymanagement.salesmodule.dto;

import java.time.LocalDateTime;

/**
* A Data Transfer Object (DTO) for encapsulating sales data.
* Includes details such as item name, quantity sold, price, and sale date.
*/
public class PerformanceSalesDto{

  private String itemName;         // Name of the item sold
  private int quantity;            // Quantity of the item sold
  private double price;            // Price per item sold
  private LocalDateTime saleDate;  // Date and time of the sale

  /**
   * Constructor to initialize all fields of SalesDTO.
   *
   * @param itemName  The name of the item sold.
   * @param quantity  The quantity of the item sold.
   * @param price     The price per item.
   * @param saleDate  The date and time of the sale.
   */
  public PerformanceSalesDto(String itemName, int quantity, double price, LocalDateTime saleDate) {
      this.itemName = itemName;
      this.quantity = quantity;
      this.price = price;
      this.saleDate = saleDate;
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
}
