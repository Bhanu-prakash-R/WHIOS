package com.inventorymanagement.vendormodule.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventorymanagement.vendormodule.dto.VendorRequestDto;
import com.inventorymanagement.vendormodule.dto.VendorResponseDto;
import com.inventorymanagement.vendormodule.entity.ContactDetails;
import com.inventorymanagement.vendormodule.entity.Vendor;
import com.inventorymanagement.vendormodule.exception.VendorNotFoundException;
import com.inventorymanagement.vendormodule.repository.VendorDao;
import com.inventorymanagement.vendormodule.service.VendorService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VendorServiceTest {

    @Mock
    private VendorDao vendorRepository;

    @InjectMocks
    private VendorService vendorService;

    private Vendor vendor;
    private VendorRequestDto vendorRequestDto;
    private ContactDetails contactDetails;
    private UUID vendorId;

    @BeforeEach
    void setUp() {
        vendorId = UUID.randomUUID();
        contactDetails = new ContactDetails();
        contactDetails.setPhoneNumber("123-456-7890");
        contactDetails.setEmail("test@example.com");
        contactDetails.setAddress("123 Main St");
        vendor = new Vendor();
        vendor.setVendorId(vendorId);
        vendor.setVendorName("Test Vendor");
        vendor.setContactDetails(contactDetails);
        vendorRequestDto = new VendorRequestDto();
        vendorRequestDto.setName("Test Vendor");
        vendorRequestDto.setPhoneNumber("123-456-7890");
        vendorRequestDto.setEmail("test@example.com");
        vendorRequestDto.setAddress("123 Main St");
    }

    @Test
    void getAllVendors_success() {
        ContactDetails contactDetails2 = new ContactDetails();
        contactDetails2.setPhoneNumber("098-765-4321");
        contactDetails2.setEmail("another@example.com");
        contactDetails2.setAddress("456 Oak Ave");
        Vendor vendor2 = new Vendor();
        vendor2.setVendorId(UUID.randomUUID());
        vendor2.setVendorName("Another Vendor");
        vendor2.setContactDetails(contactDetails2);
        List<Vendor> vendors = Arrays.asList(vendor, vendor2);
        when(vendorRepository.findAll()).thenReturn(vendors);

        List<VendorResponseDto> responseDtos = vendorService.getAllVendors();

        assertEquals(2, responseDtos.size());
        assertEquals("Test Vendor", responseDtos.get(0).getVendorName());
        assertEquals("123-456-7890", responseDtos.get(0).getContactDetails().getPhoneNumber());
        assertEquals("Another Vendor", responseDtos.get(1).getVendorName());
        assertEquals("098-765-4321", responseDtos.get(1).getContactDetails().getPhoneNumber());
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    void getVendorNames_success() {
        ContactDetails contactDetails2 = new ContactDetails();
        contactDetails2.setPhoneNumber("098-765-4321");
        contactDetails2.setEmail("another@example.com");
        contactDetails2.setAddress("456 Oak Ave");
        Vendor vendor2 = new Vendor();
        vendor2.setVendorId(UUID.randomUUID());
        vendor2.setVendorName("Another Vendor");
        vendor2.setContactDetails(contactDetails2);
        List<Vendor> vendors = Arrays.asList(vendor, vendor2);
        when(vendorRepository.findAll()).thenReturn(vendors);

        List<String> vendorNames = vendorService.getAllVendorNames();

        assertEquals(2, vendorNames.size());
        assertTrue(vendorNames.contains("Test Vendor"));
        assertTrue(vendorNames.contains("Another Vendor"));
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    void getVendorById_existingId_success() {
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        VendorResponseDto responseDto = vendorService.getVendorById(vendorId);

        assertEquals("Test Vendor", responseDto.getVendorName());
        assertEquals("123-456-7890", responseDto.getContactDetails().getPhoneNumber());
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    void getVendorById_nonExistingId_throwsVendorNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(vendorRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(VendorNotFoundException.class, () -> vendorService.getVendorById(nonExistingId));
        verify(vendorRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void saveVendor_validRequest_success() {
        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);

        VendorResponseDto responseDto = vendorService.saveVendor(vendorRequestDto);

        assertEquals("Test Vendor", responseDto.getVendorName());
        assertEquals("123-456-7890", responseDto.getContactDetails().getPhoneNumber());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    @Test
    void saveVendor_invalidRequest_throwsInvalidVendorRequestException_missingName() {
        VendorRequestDto invalidDto = new VendorRequestDto();
        invalidDto.setPhoneNumber("123-456-7890");
        invalidDto.setEmail("test@example.com");
        invalidDto.setAddress("123 Main St");

        assertThrows(RuntimeException.class, () -> vendorService.saveVendor(invalidDto));
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void saveVendor_invalidRequest_throwsInvalidVendorRequestException_missingPhone() {
        VendorRequestDto invalidDto = new VendorRequestDto();
        invalidDto.setName("Test Vendor");
        invalidDto.setEmail("test@example.com");
        invalidDto.setAddress("123 Main St");

        assertThrows(RuntimeException.class, () -> vendorService.saveVendor(invalidDto));
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void saveVendor_invalidRequest_throwsInvalidVendorRequestException_missingEmail() {
        VendorRequestDto invalidDto = new VendorRequestDto();
        invalidDto.setName("Test Vendor");
        invalidDto.setPhoneNumber("123-456-7890");
        invalidDto.setAddress("123 Main St");

        assertThrows(RuntimeException.class, () -> vendorService.saveVendor(invalidDto));
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void saveVendor_invalidRequest_throwsInvalidVendorRequestException_missingAddress() {
        VendorRequestDto invalidDto = new VendorRequestDto();
        invalidDto.setName("Test Vendor");
        invalidDto.setPhoneNumber("123-456-7890");
        invalidDto.setEmail("test@example.com");

        assertThrows(RuntimeException.class, () -> vendorService.saveVendor(invalidDto));
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void updateVendor_existingId_success() {
        VendorRequestDto updateDto = new VendorRequestDto();
        updateDto.setName("Updated Vendor");
        updateDto.setPhoneNumber("987-654-3210");
        updateDto.setEmail("updated@example.com");
        updateDto.setAddress("456 Updated St");
        ContactDetails updatedContactDetails = new ContactDetails();
        updatedContactDetails.setPhoneNumber("987-654-3210");
        updatedContactDetails.setEmail("updated@example.com");
        updatedContactDetails.setAddress("456 Updated St");
        Vendor updatedVendor = new Vendor();
        updatedVendor.setVendorId(vendorId);
        updatedVendor.setVendorName("Updated Vendor");
        updatedVendor.setContactDetails(updatedContactDetails);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any(Vendor.class))).thenReturn(updatedVendor);

        VendorResponseDto responseDto = vendorService.updateVendor(vendorId, updateDto);

        assertEquals("Updated Vendor", responseDto.getVendorName());
        assertEquals("987-654-3210", responseDto.getContactDetails().getPhoneNumber());
        verify(vendorRepository, times(1)).findById(vendorId);
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    

    @Test
    void updateVendor_invalidRequest_throwsInvalidVendorRequestException_missingName() {
        VendorRequestDto invalidDto = new VendorRequestDto();
        invalidDto.setPhoneNumber("987-654-3210");
        invalidDto.setEmail("updated@example.com");
        invalidDto.setAddress("456 Updated St");
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        assertThrows(RuntimeException.class, () -> vendorService.updateVendor(vendorId, invalidDto));
        verify(vendorRepository, times(1)).findById(vendorId);
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void deleteVendor_existingId_success() {
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        doNothing().when(vendorRepository).delete(vendor);

        vendorService.deleteVendor(vendorId);

        verify(vendorRepository, times(1)).findById(vendorId);
        verify(vendorRepository, times(1)).delete(vendor);
    }

    @Test
    void deleteVendor_nonExistingId_throwsVendorNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(vendorRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(VendorNotFoundException.class, () -> vendorService.deleteVendor(nonExistingId));
        verify(vendorRepository, times(1)).findById(nonExistingId);
        verify(vendorRepository, never()).delete(any(Vendor.class));
    }

    @Test
    void searchVendorsByName_existingName_success() {
        List<Vendor> vendors = Arrays.asList(vendor);
        when(vendorRepository.findByVendorNameContaining("Test")).thenReturn(vendors);

        List<VendorResponseDto> responseDtos = vendorService.searchVendorsByName("Test");

        assertEquals(1, responseDtos.size());
        assertEquals("Test Vendor", responseDtos.get(0).getVendorName());
        verify(vendorRepository, times(1)).findByVendorNameContaining("Test");
    }

    @Test
    void searchVendorsByName_nonExistingName_throwsVendorNotFoundException() {
        when(vendorRepository.findByVendorNameContaining("NonExistent")).thenReturn(Arrays.asList());

        assertThrows(VendorNotFoundException.class, () -> vendorService.searchVendorsByName("NonExistent"));
        verify(vendorRepository, times(1)).findByVendorNameContaining("NonExistent");
    }
}