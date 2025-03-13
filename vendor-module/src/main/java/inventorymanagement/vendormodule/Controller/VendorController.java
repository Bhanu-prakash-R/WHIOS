package inventorymanagement.vendormodule.Controller;

//package inventorymanagement.vendorModule.Controller;

import inventorymanagement.vendormodule.Dto.VendorRequestDto;
import inventorymanagement.vendormodule.Dto.VendorResponseDto;
import inventorymanagement.vendormodule.Service.VendorService;
import inventorymanagement.vendormodule.exception.VendorNotFoundException;
import jakarta.validation.Valid;
import inventorymanagement.vendormodule.exception.InvalidVendorRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;

    // Get all vendors
    @GetMapping
    public ResponseEntity<List<VendorResponseDto>> getAllVendors() {
        try {
            List<VendorResponseDto> vendors = vendorService.getAllVendors();
            return new ResponseEntity<>(vendors, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching vendors: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch vendors", e);
        }
    }
    
    // Get vendor names
    @GetMapping("/names")
    public ResponseEntity<List<String>> getVendorNames() {
        try {
            List<String> vendorNames = vendorService.getAllVendorNames();
            return ResponseEntity.ok(vendorNames);
        } catch (Exception e) {
            logger.error("Error fetching vendor names: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get a vendor by ID
    @GetMapping("/{id}")
    public ResponseEntity<VendorResponseDto> getVendorById(@PathVariable UUID id) {
        try {
            VendorResponseDto vendor = vendorService.getVendorById(id);
            return new ResponseEntity<>(vendor, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with id: " + id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error fetching vendor by id: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch vendor by id", e);
        }
    }

    // Create a new vendor
    @PostMapping
    public ResponseEntity<VendorResponseDto> createVendor( @Valid @RequestBody VendorRequestDto vendorRequestDto) {
        try {
            VendorResponseDto newVendor = vendorService.saveVendor(vendorRequestDto);
            return new ResponseEntity<>(newVendor, HttpStatus.CREATED);
        } catch (InvalidVendorRequestException e) {
            logger.error("Invalid vendor request: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error creating vendor: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create vendor", e);
        }
    }

    // Update a vendor
    @PutMapping("/{id}")
    public ResponseEntity<VendorResponseDto> updateVendor(@PathVariable UUID id, @RequestBody VendorRequestDto vendorRequestDto) {
        try {
            VendorResponseDto updatedVendor = vendorService.updateVendor(id, vendorRequestDto);
            return new ResponseEntity<>(updatedVendor, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with id: " + id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (InvalidVendorRequestException e) {
            logger.error("Invalid vendor request: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error updating vendor: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update vendor", e);
        }
    }

    // Delete a vendor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable UUID id) {
        try {
            vendorService.deleteVendor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (VendorNotFoundException e) {
            logger.error("Vendor not found with id: " + id, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error deleting vendor: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete vendor", e);
        }
    }

    // Search vendors by name
    @GetMapping("/search/{vendorName}")
    public ResponseEntity<List<VendorResponseDto>> searchVendorsByName(@PathVariable String vendorName) {
        try {
            List<VendorResponseDto> vendors = vendorService.searchVendorsByName(vendorName);
            return new ResponseEntity<>(vendors, HttpStatus.OK);
        } catch (VendorNotFoundException e) {
            logger.error("No vendors found with name containing: " + vendorName, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error searching vendors by name: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to search vendors by name", e);
        }
    }
}
