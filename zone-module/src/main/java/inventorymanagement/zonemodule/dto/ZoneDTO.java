package inventorymanagement.zonemodule.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ZoneDTO {
    private String zoneId; // Changed from Long to UUID
    private String zoneName;
    private String description;
    private Boolean isActive;
}
