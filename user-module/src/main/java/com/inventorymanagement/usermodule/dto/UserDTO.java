package com.inventorymanagement.usermodule.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class UserDTO {
    @Id
    private UUID userid; // Primary key, manually set as UUID

    private String username;
    private String passwordHash;
}

