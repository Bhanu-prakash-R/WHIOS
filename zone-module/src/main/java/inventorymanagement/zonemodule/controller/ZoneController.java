package inventorymanagement.zonemodule.controller;

import inventorymanagement.zonemodule.dto.ZoneDTO;
import inventorymanagement.zonemodule.exception.ZoneNotFoundException;
import inventorymanagement.zonemodule.response.ApiResponse;
import inventorymanagement.zonemodule.service.ZoneService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing zones in the inventory management system.
 * Provides endpoints to create, update, delete, and fetch zone-related data.
 */
@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http://localhost:3001")
public class ZoneController {
    
    // Logger instance for logging important events.
    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);
    
    // Autowired ZoneService to delegate business logic.
    @Autowired
    private ZoneService zoneService;

    /**
     * Fetches all zones.
     * 
     * @return ResponseEntity containing all ZoneDTOs wrapped in an ApiResponse.
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ZoneDTO>>> getAllZones() {
        logger.info("Fetching all zones");
        List<ZoneDTO> zones = zoneService.getAllZones();
        return ResponseEntity.ok(new ApiResponse<>(true, "Zones retrieved successfully", zones));
    }

    /**
     * Fetches all active zones.
     * 
     * @return ResponseEntity containing active ZoneDTOs wrapped in an ApiResponse.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ZoneDTO>>> getAllActiveZones() {
        logger.info("Fetching all active zones");
        List<ZoneDTO> activeZones = zoneService.getAllActiveZones();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active zones retrieved successfully", activeZones));
    }

    /**
     * Fetches the names of active zones.
     * 
     * @return ResponseEntity containing a list of active zone names wrapped in an ApiResponse.
     */
    @GetMapping("/active/names")
    public ResponseEntity<List<String>> getNamesOfActiveZones() {
        logger.info("Fetching names of active zones");
        List<String> activeZoneNames = zoneService.getNamesOfActiveZones();
        return ResponseEntity.ok(activeZoneNames);
    }

    /**
     * Counts the number of active zones.
     * 
     * @return ResponseEntity containing the count of active zones wrapped in an ApiResponse.
     */
    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> countActiveZones() {
        logger.info("Counting active zones");
        Long count = zoneService.countActiveZones();
        return ResponseEntity.ok(new ApiResponse<>(true, "Count of active zones retrieved successfully", count));
    }

    /**
     * Fetches a specific zone by its ID.
     * 
     * @param zoneId Unique identifier of the zone.
     * @return ResponseEntity containing the specified ZoneDTO wrapped in an ApiResponse.
     */
    @GetMapping("/{zoneId}")
    public ResponseEntity<ApiResponse<ZoneDTO>> getZoneById(@PathVariable String zoneId) {
        logger.info("Fetching zone with ID: {}", zoneId);
        ZoneDTO zone = zoneService.getZoneById(zoneId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Zone retrieved successfully", zone));
    }

    /**
     * Creates a new zone.
     * 
     * @param zoneDTO Data transfer object containing zone details.
     * @return ResponseEntity containing the created ZoneDTO wrapped in an ApiResponse.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ZoneDTO>> createZone(@Valid @RequestBody ZoneDTO zoneDTO) {
        logger.info("Creating a new zone");
        ZoneDTO createdZone = zoneService.createZone(zoneDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Zone created successfully", createdZone));
    }

    /**
     * Updates an existing zone.
     * 
     * @param zoneId Unique identifier of the zone to update.
     * @param zoneDTO Data transfer object containing updated zone details.
     * @return ResponseEntity containing the updated ZoneDTO wrapped in an ApiResponse.
     */
    @PutMapping("/{zoneId}")
    public ResponseEntity<ApiResponse<ZoneDTO>> updateZone(@PathVariable String zoneId, @Valid @RequestBody ZoneDTO zoneDTO) {
        logger.info("Updating zone with ID: {}", zoneId);
        ZoneDTO updatedZone = zoneService.updateZone(zoneId, zoneDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Zone updated successfully", updatedZone));
    }

    /**
     * Toggles the active status of a specific zone.
     * 
     * @param zoneId Unique identifier of the zone.
     * @return ResponseEntity containing the updated ZoneDTO with toggled status wrapped in an ApiResponse.
     */
    @PatchMapping("/{zoneId}/toggle-status")
    public ResponseEntity<ApiResponse<ZoneDTO>> toggleZoneStatus(@PathVariable String zoneId) {
        logger.info("Toggling status for zone ID: {}", zoneId);
        ZoneDTO updatedZone = zoneService.toggleZoneStatus(zoneId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Zone status toggled successfully", updatedZone));
    }

    /**
     * Deletes a specific zone.
     * 
     * @param zoneId Unique identifier of the zone to delete.
     * @return ResponseEntity confirming successful deletion of the zone.
     */
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(@PathVariable String zoneId) {
        logger.info("Deleting zone with ID: {}", zoneId);
        zoneService.deleteZone(zoneId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Zone deleted successfully", null));
    }
}
