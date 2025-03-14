package inventorymanagement.zonemodule.controller;

import inventorymanagement.zonemodule.dto.ZoneDTO;
import inventorymanagement.zonemodule.exception.ZoneNotFoundException;
import inventorymanagement.zonemodule.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);

    @Autowired
    private ZoneService zoneService;

    // Get all zones
    @GetMapping
    public ResponseEntity<?> getAllZones() {
        logger.info("Entering getAllZones()");
        try {
            List<ZoneDTO> zones = zoneService.getAllZones();
            logger.info("Successfully fetched all zones. Exiting getAllZones()");
            return ResponseEntity.ok(zones);
        } catch (Exception ex) {
            logger.error("Error in getAllZones(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error fetching zones: " + ex.getMessage());
        }
    }

    // Get all active zones
    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveZones() {
        logger.info("Entering getAllActiveZones()");
        try {
            List<ZoneDTO> activeZones = zoneService.getAllActiveZones();
            logger.info("Successfully fetched active zones. Exiting getAllActiveZones()");
            return ResponseEntity.ok(activeZones);
        } catch (Exception ex) {
            logger.error("Error in getAllActiveZones(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error fetching active zones: " + ex.getMessage());
        }
    }

    // Fetch only the names of zones that are active
    @GetMapping("/active/names")
    public ResponseEntity<?> getNamesOfActiveZones() {
        logger.info("Entering getNamesOfActiveZones()");
        try {
            List<String> activeZoneNames = zoneService.getNamesOfActiveZones();
            logger.info("Successfully fetched names of active zones. Exiting getNamesOfActiveZones()");
            return ResponseEntity.ok(activeZoneNames);
        } catch (Exception ex) {
            logger.error("Error in getNamesOfActiveZones(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error fetching names of active zones: " + ex.getMessage());
        }
    }

    // Count the number of active zones
    @GetMapping("/count/active")
    public ResponseEntity<?> countActiveZones() {
        logger.info("Entering countActiveZones()");
        try {
            Long count = zoneService.countActiveZones();
            logger.info("Successfully counted active zones. Exiting countActiveZones()");
            return ResponseEntity.ok(count);
        } catch (Exception ex) {
            logger.error("Error in countActiveZones(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error counting active zones: " + ex.getMessage());
        }
    }

    // Get zone by zoneId
    @GetMapping("/{zoneId}")
    public ResponseEntity<?> getZoneById(@PathVariable String zoneId) { // Changed from Long to UUID
        logger.info("Entering getZoneById() with Zone ID: {}", zoneId);
        try {
            ZoneDTO zone = zoneService.getZoneById(zoneId);
            logger.info("Successfully fetched zone. Exiting getZoneById()");
            return ResponseEntity.ok(zone);
        } catch (Exception ex) {
            logger.error("Error in getZoneById(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error fetching zone: " + ex.getMessage());
        }
    }

    // Create a new zone
    @PostMapping
    public ResponseEntity<?> createZone(@RequestBody ZoneDTO zoneDTO) {
        logger.info("Entering createZone()");
        try {
            ZoneDTO createdZone = zoneService.createZone(zoneDTO);
            logger.info("Successfully created zone. Exiting createZone()");
            return ResponseEntity.ok(createdZone);
        } catch (Exception ex) {
            logger.error("Error in createZone(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error creating zone: " + ex.getMessage());
        }
    }

    // Update an existing zone
    @PutMapping("/{zoneId}")
    public ResponseEntity<?> updateZone(@PathVariable String zoneId, @RequestBody ZoneDTO zoneDTO) {
        logger.info("Entering updateZone() with Zone ID: {}", zoneId);
        try {
            ZoneDTO updatedZone = zoneService.updateZone(zoneId, zoneDTO);
            logger.info("Successfully updated zone. Exiting updateZone()");
            return ResponseEntity.ok(updatedZone);
        } catch (Exception ex) {
            logger.error("Error in updateZone(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error updating zone: " + ex.getMessage());
        }
    }

    // Toggle zone status
    @PatchMapping("/{zoneId}/toggle-status")
    public ResponseEntity<?> toggleZoneStatus(@PathVariable String zoneId) {
        logger.info("Entering toggleZoneStatus() with Zone ID: {}", zoneId);
        try {
            ZoneDTO updatedZone = zoneService.toggleZoneStatus(zoneId);
            logger.info("Successfully toggled zone status. Exiting toggleZoneStatus()");
            return ResponseEntity.ok(updatedZone);
        } catch (ZoneNotFoundException ex) {
            logger.error("Error in toggleZoneStatus(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error in toggleZoneStatus(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error toggling zone status");
        }
    }

    // Delete a zone
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<?> deleteZone(@PathVariable String zoneId) {
        logger.info("Entering deleteZone() with Zone ID: {}", zoneId);
        try {
            zoneService.deleteZone(zoneId);
            logger.info("Successfully deleted zone. Exiting deleteZone()");
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error in deleteZone(): {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Error deleting zone: " + ex.getMessage());
        }
    }
}
