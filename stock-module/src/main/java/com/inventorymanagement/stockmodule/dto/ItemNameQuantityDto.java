package com.inventorymanagement.stockmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
* Lombok annotations to reduce boilerplate code.
*
* - @Data: Generates getters, setters, toString, equals, and hashCode methods.
* - @AllArgsConstructor: Generates a constructor with arguments for all fields.
*/
@Data
@AllArgsConstructor

/**
 * Data Transfer Object (DTO) for representing an item's name and its quantity.
 *
 * This class holds the item name and its corresponding quantity, 
 * typically used for transferring data between layers.
 */
public class ItemNameQuantityDto {
    private String itemName;
    private int quantity;
}
