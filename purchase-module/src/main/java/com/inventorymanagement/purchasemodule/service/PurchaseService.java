package com.inventorymanagement.purchasemodule.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.inventorymanagement.purchasemodule.client.VendorFeignClient;
import com.inventorymanagement.purchasemodule.dao.PurchaseDao;
import com.inventorymanagement.purchasemodule.dto.PurchaseDetailsDto;
import com.inventorymanagement.purchasemodule.dto.PurchaseDto;
import com.inventorymanagement.purchasemodule.dto.PurchaseMetricsDto;
import com.inventorymanagement.purchasemodule.dto.VendorResponseDto;
import com.inventorymanagement.purchasemodule.entity.Purchase;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

/**
 * Service class for managing purchase-related business logic.
 * Handles operations such as fetching, saving, and deleting purchase records.
 */
@Service
@Slf4j
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;
    @Autowired
    private VendorFeignClient vendorFeignClient;

    /**
     * Retrieves all purchases from the database.
     * 
     * @return List of PurchaseDto objects representing all purchases.
     */
    public List<PurchaseDto> getAllPurchases() {
        log.info("Entering getAllPurchases method");

        try {
            return purchaseDao.findAll()
                              .stream()
                              .map(purchase -> {
                                  // Convert entity to DTO
                                  PurchaseDto purchaseDto = convertToDto(purchase);

                                  // Calculate total price and set it in the DTO
                                  double totalPrice = purchase.getQuantity() * purchase.getPrice();
                                  purchaseDto.setPrice(totalPrice); // Override the price field with total price

                                  log.info("Purchase ID: {}, Total Price: ₹{}", purchase.getPurchaseId(), totalPrice);
                                  return purchaseDto;
                              })
                              .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all purchases: ", e);
            throw new RuntimeException("Failed to fetch purchases", e); // Proper exception handling
        } finally {
            log.info("Exiting getAllPurchases method");
        }
    }

    
    /**
     * Retrieves a specific purchase by its ID.
     * 
     * @param id the UUID of the purchase.
     * @return Optional containing the PurchaseDto if found, otherwise empty.
     */
    public Optional<PurchaseDto> getPurchaseById(UUID id) {
        return purchaseDao.findById(id).map(this::convertToDto);
    }
    
    /**
     * Saves a purchase record to the database.
     * 
     * @param purchaseDto the PurchaseDto object containing purchase details.
     * @return PurchaseDto representing the saved purchase with total price calculated.
     */
    public PurchaseDto savePurchase(PurchaseDto purchaseDto) {
        log.info("Starting the creation of a new purchase for item: {}, vendor: {}", purchaseDto.getItemName(), purchaseDto.getVendorName());
 
        // Step 1: Fetch all vendors from the Vendor module
        List<VendorResponseDto> vendors = vendorFeignClient.getAllVendors();
        log.info("Fetched {} vendors from the Vendor module.", vendors.size());
 
        // Step 2: Validate vendorId and vendorName
        vendors.stream()
            .filter(vendor -> vendor.getVendorId().equals(purchaseDto.getVendorId())
                    && vendor.getVendorName().equalsIgnoreCase(purchaseDto.getVendorName()))
            .findFirst()
            .orElseThrow(() -> {
            	log.error("Invalid vendorId: {} or vendorName: {}. Vendor not found in the Vendor module.",
                        purchaseDto.getVendorId(), purchaseDto.getVendorName());
              return new IllegalArgumentException("Invalid vendor: Vendor ID '" + purchaseDto.getVendorId() +
                                                  "' and Vendor Name '" + purchaseDto.getVendorName() + "' do not match.");
          });
 
      log.info("Vendor '{}' with ID '{}' is valid. Proceeding with the purchase creation.",
               purchaseDto.getVendorName(), purchaseDto.getVendorId());
 
        // Step 3: Convert DTO to entity
        Purchase purchase = convertToEntity(purchaseDto);
        purchase.setVendorId(purchaseDto.getVendorId()); // Set the validated vendorId in the entity
 
        // Step 4: Save the purchase in the database
        Purchase savedPurchase = purchaseDao.save(purchase);
 
        // Step 5: Convert the saved purchase entity to a DTO
        PurchaseDto responseDto = convertToDto(savedPurchase);
 
        // Step 6: Replace the price with the calculated total price (price * quantity)
        responseDto.setPrice(savedPurchase.getPrice() * savedPurchase.getQuantity());
        log.info("Purchase saved with total price: {}", responseDto.getPrice());
 
        // Step 7: Return the response DTO
        return responseDto;
    }


    /**
     * Deletes a purchase record by its ID.
     * 
     * @param id the UUID of the purchase to delete.
     */
    public void deletePurchase(UUID id) {
        purchaseDao.deleteById(id);
    }
    
    /**
     * Converts a Purchase entity to its corresponding DTO representation.
     * 
     * @param purchase the Purchase entity to convert.
     * @return PurchaseDto object.
     */
    public PurchaseDto convertToDto(Purchase purchase) {
        PurchaseDto dto = new PurchaseDto();
        dto.setPurchaseId(purchase.getPurchaseId());
        dto.setQuantity(purchase.getQuantity());
        dto.setPrice(purchase.getPrice());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setVendorId(purchase.getVendorId());
        dto.setCategory(purchase.getCategory());
        dto.setVendorName(purchase.getVendorName());
        dto.setItemName(purchase.getItemName());
        return dto;
    }
    
    /**
     * Converts a PurchaseDto object to its corresponding entity representation.
     * 
     * @param dto the PurchaseDto to convert.
     * @return Purchase entity.
     */
    public Purchase convertToEntity(PurchaseDto dto) {
        Purchase purchase = new Purchase();
        purchase.setPurchaseId(dto.getPurchaseId());
        purchase.setQuantity(dto.getQuantity());
        purchase.setPrice(dto.getPrice());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setVendorId(dto.getVendorId());
        purchase.setCategory(dto.getCategory());
        purchase.setVendorName(dto.getVendorName());
        purchase.setItemName(dto.getItemName());
        return purchase;
    }
    
    /**
     * Retrieves the names of all vendors by invoking the vendor module via FeignClient.
     * 
     * @return List of vendor names as strings.
    public List<VendorResponseDto> getAllVendors() {
        return vendorFeignClient.getAllVendors();
    }
    public List<VendorResponseDto> getAllVendors() {
        return vendorFeignClient.getAllVendors();
    }
    
     * Retrieves distinct item names from all purchase records.
     * 
     * @return List of unique item names as strings.
     */
    public List<VendorResponseDto> getAllVendors() {
        return vendorFeignClient.getAllVendors();
    }
    public List<String> getItemNames() {
        // Fetch all purchase records from the repository
        List<Purchase> purchases = purchaseDao.findAll();

        // Extract item names and ensure no duplicates using distinct()
        return purchases.stream()
                .map(Purchase::getItemName)
                .distinct()
                .collect(Collectors.toList());
    }
    public List<String> getVendorNames(){
    	log.info("Fetching all vendor names");
    	return vendorFeignClient.getVendorNames();
    }
    
    /**
     * Fetches recent purchase metrics with pagination support.
     * 
     * @param pageable the Pageable object for pagination configuration.
     * @return List of PurchaseMetricsDto representing the recent purchases.
     */
    public List<PurchaseMetricsDto> getRecentPurchases(Pageable pageable) {
        // Call the repository method to fetch recent purchases
        return purchaseDao.findRecentPurchases(pageable);
    }

    public PurchaseDetailsDto getLimitedPurchaseDetailsByItemName(String itemName) {
        List<Purchase> purchases = purchaseDao.findByItemName(itemName);

        if (purchases.isEmpty()) {
            log.error("No purchase found for item '{}'", itemName);
            throw new IllegalArgumentException("No purchase found for item: " + itemName);
        }

        // Find the latest purchase based on purchaseDate
        Purchase latestPurchase = purchases.stream()
            .max(Comparator.comparing(Purchase::getPurchaseDate)) // Sort by purchaseDate
            .orElseThrow(() -> new IllegalArgumentException("Unable to find the latest purchase for item: " + itemName));

        // Calculate total price as quantity × price per item
        double totalPrice = latestPurchase.getPrice();// latestPurchase.getQuantity();// * latestPurchase.getPrice();

        return new PurchaseDetailsDto(
            latestPurchase.getVendorName(),
            latestPurchase.getQuantity(),
            totalPrice, // Use total price instead of per-item price
            latestPurchase.getCategory(),
            latestPurchase.getPurchaseDate() // Include purchaseDate in the DTO
        );
    }



}
