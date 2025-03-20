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
import java.util.stream.Collectors;

/**
 * Service class for managing operations related to zones.
 * Provides business logic for creating, updating, deleting, and fetching zones.
 * Uses ZoneRepository for database interactions and includes logging for debugging and monitoring.
 */
@Service
public class ZoneService {

    // Logger instance for capturing and recording application events
    private static final Logger logger = LoggerFactory.getLogger(ZoneService.class);

    @Autowired
    private ZoneRepository zoneRepository;

    /**
     * Fetches all zones from the database.
     * 
     * @return A list of ZoneDTO objects representing all zones.
     */
    public List<ZoneDTO> getAllZones() {
        logger.info("Entering getAllZones()");
        List<ZoneDTO> zones = zoneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        logger.info("Exiting getAllZones() with {} zones found.", zones.size());
        return zones;
    }

    /**
     * Fetches all active zones from the database.
     * 
     * @return A list of ZoneDTO objects representing active zones.
     */
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

    /**
     * Fetches the names of all active zones.
     * 
     * @return A list of Strings representing the names of active zones.
     * @throws ZoneNotFoundException if no active zones are found.
     */
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

    /**
     * Counts the number of active zones.
     * 
     * @return A Long value representing the count of active zones.
     * @throws ZoneNotFoundException if no active zones are found.
     */
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

    /**
     * Fetches a zone by its ID.
     * 
     * @param zoneId The ID of the zone.
     * @return A ZoneDTO object representing the requested zone.
     * @throws ZoneNotFoundException if the zone with the specified ID is not found.
     */
    public ZoneDTO getZoneById(String zoneId) {
        logger.info("Entering getZoneById() with Zone ID: {}", zoneId);
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found."));
        ZoneDTO zoneDTO = convertToDTO(zone);
        logger.info("Exiting getZoneById() with zone: {}", zoneDTO);
        return zoneDTO;
    }

    /**
     * Toggles the active status of a zone.
     * 
     * @param zoneId The ID of the zone.
     * @return A ZoneDTO object representing the updated zone.
     * @throws ZoneNotFoundException if the zone with the specified ID is not found.
     */
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

    /**
     * Creates a new zone.
     * 
     * @param zoneDTO A ZoneDTO object containing the details of the new zone.
     * @return A ZoneDTO object representing the created zone.
     */
    public ZoneDTO createZone(ZoneDTO zoneDTO) {
        logger.info("Entering createZone()");
        Zone zone = convertToEntity(zoneDTO);
        Zone savedZone = zoneRepository.save(zone);
        ZoneDTO savedZoneDTO = convertToDTO(savedZone);
        logger.info("Exiting createZone() with created zone: {}", savedZoneDTO);
        return savedZoneDTO;
    }

    /**
     * Updates an existing zone.
     * 
     * @param zoneId The ID of the zone to update.
     * @param zoneDTO A ZoneDTO object containing updated details.
     * @return A ZoneDTO object representing the updated zone.
     * @throws ZoneNotFoundException if the zone with the specified ID is not found.
     */
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

    /**
     * Deletes a zone by its ID.
     * 
     * @param zoneId The ID of the zone to delete.
     * @throws ZoneNotFoundException if the zone with the specified ID is not found.
     */
    public void deleteZone(String zoneId) {
        logger.info("Entering deleteZone() with Zone ID: {}", zoneId);
        if (!zoneRepository.existsById(zoneId)) {
            logger.error("Zone with Zone ID {} not found in deleteZone()", zoneId);
            throw new ZoneNotFoundException("Zone with Zone ID " + zoneId + " not found.");
        }
        zoneRepository.deleteById(zoneId);
        logger.info("Exiting deleteZone() with Zone ID: {}", zoneId);
    }

    /**
     * Converts a Zone entity to a ZoneDTO.
     * 
     * @param zone The Zone entity to convert.
     * @return A ZoneDTO object.
     */
    private ZoneDTO convertToDTO(Zone zone) {
        ZoneDTO zoneDTO = new ZoneDTO();
        zoneDTO.setZoneId(zone.getZoneId());
        zoneDTO.setZoneName(zone.getZoneName());
        zoneDTO.setDescription(zone.getDescription());
        zoneDTO.setIsActive(zone.getIsActive());
        return zoneDTO;
    }

    /**
     * Converts a ZoneDTO to a Zone entity.
     * 
     * @param zoneDTO The ZoneDTO object to convert.
     * @return A Zone entity.
     */
    private Zone convertToEntity(ZoneDTO zoneDTO) {
        Zone zone = new Zone();
        zone.setZoneName(zoneDTO.getZoneName());
        zone.setDescription(zoneDTO.getDescription());
        zone.setIsActive(zoneDTO.getIsActive());
        return zone;
    }
}
