package com.inventorymanagement.zonemodule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for the Zone entity.
 * Used to encapsulate zone-related data for communication between different layers of the application.
 * Implements data validation using Jakarta Bean Validation annotations.
 */
@Data
public class ZoneDTO {

    /**
     * Unique identifier for the zone.
     * Note: No validation is required for this field as it is typically auto-generated.
     */
    private String zoneId;

    /**
     * Name of the zone.
     * Must not be blank to ensure meaningful and descriptive names.
     * Validation: {@code @NotBlank} ensures the field is not null or empty.
     */
    @NotBlank(message = "Zone name cannot be blank")
    private String zoneName;

    /**
     * Description of the zone.
     * Must not be blank to provide clarity on the purpose or scope of the zone.
     * Validation: {@code @NotBlank} ensures the field is not null or empty.
     */
    @NotBlank(message = "Description cannot be blank")
    private String description;

    /**
     * Active status of the zone.
     * Indicates whether the zone is currently active or inactive.
     * Validation: {@code @NotNull} ensures the field is not null.
     */
    @NotNull(message = "IsActive cannot be null")
    private Boolean isActive;
}
