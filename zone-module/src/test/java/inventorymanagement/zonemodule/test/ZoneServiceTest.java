package inventorymanagement.zonemodule.test;


import inventorymanagement.zonemodule.dto.ZoneDTO;
import inventorymanagement.zonemodule.entity.Zone;
import inventorymanagement.zonemodule.exception.ZoneAlreadyExistsException;
import inventorymanagement.zonemodule.exception.ZoneNotFoundException;
import inventorymanagement.zonemodule.repository.ZoneRepository;
import inventorymanagement.zonemodule.service.ZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private ZoneService zoneService;

    private Zone zone1;
    private Zone zone2;
    private ZoneDTO zoneDTO1;
    private ZoneDTO zoneDTO2;

    @BeforeEach
    void setUp() {
        zone1 = new Zone();
        zone1.setZoneId("ZNE001");
        zone1.setZoneName("Zone A");
        zone1.setDescription("Description for Zone A");
        zone1.setIsActive(true);

        zone2 = new Zone();
        zone2.setZoneId("ZNE002");
        zone2.setZoneName("Zone B");
        zone2.setDescription("Description for Zone B");
        zone2.setIsActive(false);

        zoneDTO1 = new ZoneDTO();
        zoneDTO1.setZoneId("ZNE001");
        zoneDTO1.setZoneName("Zone A");
        zoneDTO1.setDescription("Description for Zone A");
        zoneDTO1.setIsActive(true);

        zoneDTO2 = new ZoneDTO();
        zoneDTO2.setZoneId("ZNE002");
        zoneDTO2.setZoneName("Zone B");
        zoneDTO2.setDescription("Description for Zone B");
        zoneDTO2.setIsActive(false);
    }

    @Test
    void getAllZones_success() {
        when(zoneRepository.findAll()).thenReturn(Arrays.asList(zone1, zone2));

        List<ZoneDTO> zones = zoneService.getAllZones();

        assertEquals(2, zones.size());
        assertEquals(zoneDTO1, zones.get(0));
        assertEquals(zoneDTO2, zones.get(1));
        verify(zoneRepository, times(1)).findAll();
    }

    @Test
    void getAllActiveZones_success() {
        when(zoneRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(zone1));

        List<ZoneDTO> activeZones = zoneService.getAllActiveZones();

        assertEquals(1, activeZones.size());
        assertEquals(zoneDTO1, activeZones.get(0));
        verify(zoneRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void getAllActiveZones_noActiveZones() {
        when(zoneRepository.findByIsActiveTrue()).thenReturn(Arrays.asList());

        List<ZoneDTO> activeZones = zoneService.getAllActiveZones();

        assertTrue(activeZones.isEmpty());
        verify(zoneRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void getNamesOfActiveZones_success() {
        when(zoneRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(zone1));

        List<String> activeZoneNames = zoneService.getNamesOfActiveZones();

        assertEquals(1, activeZoneNames.size());
        assertEquals("Zone A", activeZoneNames.get(0));
        verify(zoneRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void getNamesOfActiveZones_noActiveZones_throwsZoneNotFoundException() {
        when(zoneRepository.findByIsActiveTrue()).thenReturn(Arrays.asList());

        assertThrows(ZoneNotFoundException.class, () -> zoneService.getNamesOfActiveZones());
        verify(zoneRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void countActiveZones_success() {
        when(zoneRepository.countByIsActiveTrue()).thenReturn(1L);

        Long count = zoneService.countActiveZones();

        assertEquals(1, count);
        verify(zoneRepository, times(1)).countByIsActiveTrue();
    }

    @Test
    void countActiveZones_noActiveZones_throwsZoneNotFoundException() {
        when(zoneRepository.countByIsActiveTrue()).thenReturn(0L);

        assertThrows(ZoneNotFoundException.class, () -> zoneService.countActiveZones());
        verify(zoneRepository, times(1)).countByIsActiveTrue();
    }

    @Test
    void getZoneById_existingId_success() {
        when(zoneRepository.findById("ZNE001")).thenReturn(Optional.of(zone1));

        ZoneDTO zone = zoneService.getZoneById("ZNE001");

        assertEquals(zoneDTO1, zone);
        verify(zoneRepository, times(1)).findById("ZNE001");
    }

    @Test
    void getZoneById_nonExistingId_throwsZoneNotFoundException() {
        when(zoneRepository.findById("NONEXIST")).thenReturn(Optional.empty());

        assertThrows(ZoneNotFoundException.class, () -> zoneService.getZoneById("NONEXIST"));
        verify(zoneRepository, times(1)).findById("NONEXIST");
    }

    @Test
    void createZone_success() {
        Zone newZone = new Zone();
        newZone.setZoneName("Zone C");
        newZone.setDescription("Description for Zone C");
        newZone.setIsActive(true);
        ZoneDTO newZoneDTO = new ZoneDTO();
        newZoneDTO.setZoneName("Zone C");
        newZoneDTO.setDescription("Description for Zone C");
        newZoneDTO.setIsActive(true);
        when(zoneRepository.findByZoneName("Zone C")).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).thenReturn(newZone);

        ZoneDTO createdZone = zoneService.createZone(newZoneDTO);

        assertEquals(newZoneDTO.getZoneName(), createdZone.getZoneName());
        assertEquals(newZoneDTO.getDescription(), createdZone.getDescription());
        assertEquals(newZoneDTO.getIsActive(), createdZone.getIsActive());
        verify(zoneRepository, times(1)).findByZoneName("Zone C");
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    @Test
    void createZone_alreadyExists_throwsZoneAlreadyExistsException() {
        ZoneDTO existingZoneDTO = new ZoneDTO();
        existingZoneDTO.setZoneName("Zone A");
        when(zoneRepository.findByZoneName("Zone A")).thenReturn(Optional.of(zone1));

        assertThrows(ZoneAlreadyExistsException.class, () -> zoneService.createZone(existingZoneDTO));
        verify(zoneRepository, times(1)).findByZoneName("Zone A");
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void updateZone_existingId_success() {
        ZoneDTO updatedZoneDTO = new ZoneDTO();
        updatedZoneDTO.setZoneName("Updated Zone A");
        updatedZoneDTO.setDescription("Updated Description for Zone A");
        updatedZoneDTO.setIsActive(false);
        Zone updatedZone = new Zone();
        updatedZone.setZoneId("ZNE001");
        updatedZone.setZoneName("Updated Zone A");
        updatedZone.setDescription("Updated Description for Zone A");
        updatedZone.setIsActive(false);
        when(zoneRepository.findById("ZNE001")).thenReturn(Optional.of(zone1));
        when(zoneRepository.save(any(Zone.class))).thenReturn(updatedZone);

        ZoneDTO resultDTO = zoneService.updateZone("ZNE001", updatedZoneDTO);

        assertEquals(updatedZoneDTO.getZoneName(), resultDTO.getZoneName());
        assertEquals(updatedZoneDTO.getDescription(), resultDTO.getDescription());
        assertEquals(updatedZoneDTO.getIsActive(), resultDTO.getIsActive());
        verify(zoneRepository, times(1)).findById("ZNE001");
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    @Test
    void updateZone_nonExistingId_throwsZoneNotFoundException() {
        ZoneDTO updatedZoneDTO = new ZoneDTO();
        updatedZoneDTO.setZoneName("Updated Zone");
        when(zoneRepository.findById("NONEXIST")).thenReturn(Optional.empty());

        assertThrows(ZoneNotFoundException.class, () -> zoneService.updateZone("NONEXIST", updatedZoneDTO));
        verify(zoneRepository, times(1)).findById("NONEXIST");
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void toggleZoneStatus_existingId_success() {
        when(zoneRepository.findById("ZNE001")).thenReturn(Optional.of(zone1));
        Zone toggledZone = new Zone();
        toggledZone.setZoneId("ZNE001");
        toggledZone.setZoneName("Zone A");
        toggledZone.setDescription("Description for Zone A");
        toggledZone.setIsActive(false);
        when(zoneRepository.save(any(Zone.class))).thenReturn(toggledZone);

        ZoneDTO resultDTO = zoneService.toggleZoneStatus("ZNE001");

        assertFalse(resultDTO.getIsActive());
        verify(zoneRepository, times(1)).findById("ZNE001");
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    @Test
    void toggleZoneStatus_nonExistingId_throwsZoneNotFoundException() {
        when(zoneRepository.findById("NONEXIST")).thenReturn(Optional.empty());

        assertThrows(ZoneNotFoundException.class, () -> zoneService.toggleZoneStatus("NONEXIST"));
        verify(zoneRepository, times(1)).findById("NONEXIST");
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void deleteZone_existingId_success() {
        when(zoneRepository.existsById("ZNE001")).thenReturn(true);
        doNothing().when(zoneRepository).deleteById("ZNE001");

        zoneService.deleteZone("ZNE001");

        verify(zoneRepository, times(1)).existsById("ZNE001");
        verify(zoneRepository, times(1)).deleteById("ZNE001");
    }

    @Test
    void deleteZone_nonExistingId_throwsZoneNotFoundException() {
        when(zoneRepository.existsById("NONEXIST")).thenReturn(false);

        assertThrows(ZoneNotFoundException.class, () -> zoneService.deleteZone("NONEXIST"));
        verify(zoneRepository, times(1)).existsById("NONEXIST");
        verify(zoneRepository, never()).deleteById("NONEXIST");
    }
}