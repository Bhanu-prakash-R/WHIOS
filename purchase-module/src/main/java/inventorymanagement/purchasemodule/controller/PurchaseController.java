package inventorymanagement.purchasemodule.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inventorymanagement.purchasemodule.dto.PurchaseDetailsDto;
import inventorymanagement.purchasemodule.dto.PurchaseDto;
import inventorymanagement.purchasemodule.dto.PurchaseMetricsDto;
import inventorymanagement.purchasemodule.exception.ResourceNotFoundException;
import inventorymanagement.purchasemodule.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;




import java.util.List;
import java.util.UUID;

/**
 * Controller class for managing purchase-related operations.
 * Provides endpoints for CRUD operations and specific queries on purchase data.
 */
@RestController
@RequestMapping("/api/purchases")
@Slf4j
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;
    
    /**
     * Retrieves a list of all purchases.
     * 
     * @return ResponseEntity containing the list of purchases and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        log.info("Fetching all purchases");
        List<PurchaseDto> purchases = purchaseService.getAllPurchases();
        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }
    
    /**
     * Retrieves a specific purchase by its ID.
     * 
     * @param id the UUID of the purchase to retrieve.
     * @return ResponseEntity containing the purchase details and HTTP status OK, or NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDto> getPurchaseById(@PathVariable UUID id) {
        log.info("Fetching purchase with ID: {}", id);
        try {
            PurchaseDto purchaseDTO = purchaseService.getPurchaseById(id)
                                                     .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));
            return new ResponseEntity<>(purchaseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            log.error("Purchase not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Creates a new purchase record.
     * 
     * @param purchaseDTO the purchase data to be created.
     * @return ResponseEntity containing the saved purchase details and HTTP status CREATED.
     */
    @PostMapping("/create")
    public ResponseEntity<PurchaseDto> createPurchase(@Valid @RequestBody PurchaseDto purchaseDTO) {
        log.info("Creating new purchase: {}", purchaseDTO);
        PurchaseDto savedPurchaseDTO = purchaseService.savePurchase(purchaseDTO);
        return new ResponseEntity<>(savedPurchaseDTO, HttpStatus.CREATED);
    }
    
    /**
     * Deletes a purchase by its ID.
     * 
     * @param id the UUID of the purchase to delete.
     * @return ResponseEntity with HTTP status NO_CONTENT if deleted, or NOT_FOUND if the purchase does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable UUID id) {
        log.info("Deleting purchase with ID: {}", id);
        try {
            purchaseService.deletePurchase(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            log.error("Purchase not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Fetches all vendor names from the vendor module.
     * 
     * @return ResponseEntity containing a list of vendor names and HTTP status OK.
     */
    @GetMapping("/vendorNames")
    public ResponseEntity<List<String>> getVendorNames(){
    	log.info("fetching vendor names");
    	List<String> vendorNames=purchaseService.getVendorNames();
    	 return ResponseEntity.ok(vendorNames);
    }
    
    /**
     * Fetches all item names related to purchases.
     * 
     * @return ResponseEntity containing a list of item names and HTTP status OK.
     */
    @GetMapping("/itemNames")
    public ResponseEntity<List<String>> getItemNames(){
    	log.info("Fetching itemNames");
    	List<String> items=purchaseService.getItemNames();
    	return ResponseEntity.ok(items);
    }
    
    /**
     * Retrieves recent purchase metrics with pagination support.
     * 
     * @param page the page number to retrieve (default is 0).
     * @param size the number of items per page (default is 3).
     * @return ResponseEntity containing the list of recent purchases and HTTP status OK.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<PurchaseMetricsDto>> getRecentPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        // Create a pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Fetch recent purchases using the service
        List<PurchaseMetricsDto> recentPurchases = purchaseService.getRecentPurchases(pageable);

        return ResponseEntity.ok(recentPurchases);
    }
    
    
    @GetMapping("/autofill/{itemName}")
    public ResponseEntity<PurchaseDetailsDto> getLimitedPurchaseDetails(@PathVariable String itemName) {
        PurchaseDetailsDto purchaseDetails = purchaseService.getLimitedPurchaseDetailsByItemName(itemName);
        if (purchaseDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(purchaseDetails);
    }
}
