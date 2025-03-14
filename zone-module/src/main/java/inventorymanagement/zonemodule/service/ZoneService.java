package inventorymanagement.zonemodule.service;

import inventorymanagement.zonemodule.dto.ZoneDTO;
import inventorymanagement.zonemodule.entity.Zone;
import inventorymanagement.zonemodule.exception.ZoneNotFoundException;
import inventorymanagement.zonemodule.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    private static final Logger logger = LoggerFactory.getLogger(ZoneService.class);

    @Autowired
    private ZoneRepository zoneRepository;

    // Get all zones
    public List<ZoneDTO> getAllZones() {
        logger.info("Entering getAllZones()");
        List<ZoneDTO> zones = zoneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Exiting getAllZones() with {} zones found.", zones.size());
        return zones;
    }

    // Get all active zones
    public List<ZoneDTO> getAllActiveZones() {
        logger.info("Entering getAllActiveZones()");
        List<ZoneDTO> activeZones = zoneRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        if (activeZones.isEmpty()) {
            logger.warn("No active zones found in getAllActiveZones()");
        }
        logger.info("Exiting getAllActiveZones() with {} active zones found.", activeZones.size());
        return activeZones;
    }

    // Fetch only the names of zones that are active
    public List<String> getNamesOfActiveZones() {
        logger.info("Entering getNamesOfActiveZones()");
        List<Zone> activeZones = zoneRepository.findByIsActiveTrue();
        if (activeZones.isEmpty()) {
            logger.warn("No active zones found in getNamesOfActiveZones()");
            throw new ZoneNotFoundException("No active zones found.");
        }
        List<String> activeZoneNames = activeZones.stream()
                .map(Zone::getZoneName)
                .collect(Collectors.toList());
        logger.info("Exiting getNamesOfActiveZones() with {} active zone names found.", activeZoneNames.size());
        return activeZoneNames;
    }

    // Count the number of active zones
    public Long countActiveZones() {
        logger.info("Entering countActiveZones()");
        Long count = zoneRepository.countByIsActiveTrue();
        if (count == 0) {
            logger.warn("No active zones to count in countActiveZones()");
            throw new ZoneNotFoundException("No active zones found to count.");
        }
        logger.info("Exiting countActiveZones() with count: {}", count);
        return count;
    }

    // Get zone by zoneId
    public ZoneDTO getZoneById(String zoneId) { // Changed from Long to UUID
        logger.info("Entering getZoneById() with Zone ID: {}", zoneId);
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found."));
        ZoneDTO zoneDTO = convertToDTO(zone);
        logger.info("Exiting getZoneById() with zone: {}", zoneDTO);
        return zoneDTO;
    }


    // Toggle the active status of a zone
    public ZoneDTO toggleZoneStatus(String zoneId) {
        logger.info("Entering toggleZoneStatus() with Zone ID: {}", zoneId);

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> {
                    logger.error("Zone with Zone ID {} not found in toggleZoneStatus()", zoneId);
                    return new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found.");
                });

        // Toggle the isActive status
        zone.setIsActive(!zone.getIsActive());
        Zone updatedZone = zoneRepository.save(zone);

        ZoneDTO updatedZoneDTO = convertToDTO(updatedZone);
        logger.info("Exiting toggleZoneStatus() with updated zone: {}", updatedZoneDTO);
        return updatedZoneDTO;
    }

    // Create a new zone
    public ZoneDTO createZone(ZoneDTO zoneDTO) {
        logger.info("Entering createZone()");
        Zone zone = convertToEntity(zoneDTO);
        Zone savedZone = zoneRepository.save(zone);
        ZoneDTO savedZoneDTO = convertToDTO(savedZone);
        logger.info("Exiting createZone() with created zone: {}", savedZoneDTO);
        return savedZoneDTO;
    }

    // Update an existing zone
    public ZoneDTO updateZone(String zoneId, ZoneDTO zoneDTO) {
        logger.info("Entering updateZone() with Zone ID: {}", zoneId);
        Zone existingZone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> {
                    logger.error("Zone with Zone ID {} not found in updateZone()", zoneId);
                    return new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found.");
                });
        existingZone.setZoneName(zoneDTO.getZoneName());
        existingZone.setDescription(zoneDTO.getDescription());
        existingZone.setIsActive(zoneDTO.getIsActive());
        Zone updatedZone = zoneRepository.save(existingZone);
        ZoneDTO updatedZoneDTO = convertToDTO(updatedZone);
        logger.info("Exiting updateZone() with updated zone: {}", updatedZoneDTO);
        return updatedZoneDTO;
    }

    // Delete a zone
    public void deleteZone(String zoneId) {
        logger.info("Entering deleteZone() with Zone ID: {}", zoneId);
        if (!zoneRepository.existsById(zoneId)) {
            logger.error("Zone with Zone ID {} not found in deleteZone()", zoneId);
            throw new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found.");
        }
        zoneRepository.deleteById(zoneId);
        logger.info("Exiting deleteZone() with Zone ID: {}", zoneId);
    }

    // Convert Zone entity to ZoneDTO
    private ZoneDTO convertToDTO(Zone zone) {
        ZoneDTO zoneDTO = new ZoneDTO();
        zoneDTO.setZoneId(zone.getZoneId()); // Updated field name
        zoneDTO.setZoneName(zone.getZoneName());
        zoneDTO.setDescription(zone.getDescription());
        zoneDTO.setIsActive(zone.getIsActive());
        return zoneDTO;
    }

    // Convert ZoneDTO to Zone entity
    private Zone convertToEntity(ZoneDTO zoneDTO) {
        Zone zone = new Zone();
        zone.setZoneName(zoneDTO.getZoneName());
        zone.setDescription(zoneDTO.getDescription());
        zone.setIsActive(zoneDTO.getIsActive());
        return zone;
    }
}
