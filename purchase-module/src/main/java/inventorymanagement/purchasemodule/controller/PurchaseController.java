package inventorymanagement.purchasemodule.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inventorymanagement.purchasemodule.dto.PurchaseDto;
import inventorymanagement.purchasemodule.exception.ResourceNotFoundException;
import inventorymanagement.purchasemodule.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;




import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@Slf4j
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        log.info("Fetching all purchases");
        List<PurchaseDto> purchases = purchaseService.getAllPurchases();
        return new ResponseEntity<>(purchases, HttpStatus.OK);
    }

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

    @PostMapping("/create")
    public ResponseEntity<PurchaseDto> createPurchase(@RequestBody PurchaseDto purchaseDTO) {
        log.info("Creating new purchase: {}", purchaseDTO);
        PurchaseDto savedPurchaseDTO = purchaseService.savePurchase(purchaseDTO);
        return new ResponseEntity<>(savedPurchaseDTO, HttpStatus.CREATED);
    }

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
    @GetMapping("/vendorNames")
    public ResponseEntity<List<String>> getVendorNames(){
    	log.info("fetching vendor names");
    	List<String> vendorNames=purchaseService.getVendorNames();
    	 return ResponseEntity.ok(vendorNames);
    }
    @GetMapping("/itemNames")
    public ResponseEntity<List<String>> getItemNames(){
    	log.info("Fetching itemNames");
    	List<String> items=purchaseService.getItemNames();
    	return ResponseEntity.ok(items);
    }
}
