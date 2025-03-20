package inventorymanagement.vendormodule.Controller;

//package inventorymanagement.vendorModule.Controller;

import inventorymanagement.vendormodule.Dto.VendorRequestDto;
import inventorymanagement.vendormodule.Dto.VendorResponseDto;
import inventorymanagement.vendormodule.Service.VendorService;
import inventorymanagement.vendormodule.exception.VendorNotFoundException;
import inventorymanagement.vendormodule.response.ApiResponse;
import jakarta.validation.Valid;
import inventorymanagement.vendormodule.exception.InvalidVendorRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;

    /**
     * Get all vendors.
     *
     * @return ApiResponse containing the list of all vendors.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorResponseDto>>> getAllVendors() {
        logger.info("Fetching all vendors");
        List<VendorResponseDto> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Vendors retrieved successfully", 
                vendors, 
                HttpStatus.OK.value(), 
                LocalDateTime.now()
            )
        );
    }

    /**
     * Get names of all vendors.
     *
     * @return ApiResponse containing the list of vendor names.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getVendorNames() {
        logger.info("Fetching all vendor names");
        List<String> vendorNames = vendorService.getAllVendorNames();
        return ResponseEntity.ok(vendorNames
            
        );
    }

    /**
     * Get a vendor by ID.
     *
     * @param id The vendor's unique identifier.
     * @return ApiResponse containing the vendor details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponseDto>> getVendorById(@PathVariable UUID id) {
        logger.info("Fetching vendor with ID: {}", id);
        VendorResponseDto vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Vendor retrieved successfully", 
                vendor, 
                HttpStatus.OK.value(), 
                LocalDateTime.now()
            )
        );
    }

    /**
     * Create a new vendor.
     *
     * @param vendorRequestDto The details of the vendor to create.
     * @return ApiResponse containing the created vendor details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponseDto>> createVendor(@Valid @RequestBody VendorRequestDto vendorRequestDto) {
        logger.info("Creating a new vendor");
        VendorResponseDto newVendor = vendorService.saveVendor(vendorRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<>(
                true, 
                "Vendor created successfully", 
                newVendor, 
                HttpStatus.CREATED.value(), 
                LocalDateTime.now()
            )
        );
    }

    /**
     * Update an existing vendor.
     *
     * @param id               The vendor's unique identifier.
     * @param vendorRequestDto The updated vendor details.
     * @return ApiResponse containing the updated vendor details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponseDto>> updateVendor(@PathVariable UUID id, @Valid @RequestBody VendorRequestDto vendorRequestDto) {
        logger.info("Updating vendor with ID: {}", id);
        VendorResponseDto updatedVendor = vendorService.updateVendor(id, vendorRequestDto);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Vendor updated successfully", 
                updatedVendor, 
                HttpStatus.OK.value(), 
                LocalDateTime.now()
            )
        );
    }

    /**
     * Delete a vendor.
     *
     * @param id The vendor's unique identifier.
     * @return ApiResponse confirming vendor deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable UUID id) {
        logger.info("Deleting vendor with ID: {}", id);
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Vendor deleted successfully", 
                null, 
                HttpStatus.OK.value(), 
                LocalDateTime.now()
            )
        );
    }

    /**
     * Search vendors by name.
     *
     * @param vendorName The name to search for.
     * @return ApiResponse containing the list of vendors matching the name.
     */
    @GetMapping("/search/{vendorName}")
    public ResponseEntity<ApiResponse<List<VendorResponseDto>>> searchVendorsByName(@PathVariable String vendorName) {
        logger.info("Searching vendors by name: {}", vendorName);
        List<VendorResponseDto> vendors = vendorService.searchVendorsByName(vendorName);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Vendors retrieved successfully", 
                vendors, 
                HttpStatus.OK.value(), 
                LocalDateTime.now()
            )
        );
    }
}