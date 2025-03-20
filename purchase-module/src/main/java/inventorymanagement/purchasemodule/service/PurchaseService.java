package inventorymanagement.purchasemodule.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import inventorymanagement.purchasemodule.Client.VendorFeignClient;
import inventorymanagement.purchasemodule.dao.PurchaseDao;
import inventorymanagement.purchasemodule.dto.PurchaseDto;
import inventorymanagement.purchasemodule.dto.PurchaseMetricsDto;
import inventorymanagement.purchasemodule.entity.Purchase;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;
    @Autowired
    private VendorFeignClient vendorFeignClient;

    public List<PurchaseDto> getAllPurchases() {
        return purchaseDao.findAll().stream()
                          .map(this::convertToDto)
                          .collect(Collectors.toList());
    }

    public Optional<PurchaseDto> getPurchaseById(UUID id) {
        return purchaseDao.findById(id).map(this::convertToDto);
    }

    public PurchaseDto savePurchase(PurchaseDto purchaseDto) {
        // Convert DTO to entity
        Purchase purchase = convertToEntity(purchaseDto);

        // Save the purchase in the database
        Purchase savedPurchase = purchaseDao.save(purchase);

        // Convert the saved purchase entity to a DTO
        PurchaseDto responseDto = convertToDto(savedPurchase);

        // Replace the price with the calculated total price
        responseDto.setPrice(savedPurchase.getPrice() * savedPurchase.getQuantity());

        // Log the calculated total price for debugging
        log.info("Purchase saved with total price: {}", responseDto.getPrice());

        return responseDto; // Return the response with total price as the "price" field
    }


    public void deletePurchase(UUID id) {
        purchaseDao.deleteById(id);
    }

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
    public List<String> getVendorNames(){
    	return vendorFeignClient.getVendorNames();
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
    
    public List<PurchaseMetricsDto> getRecentPurchases(Pageable pageable) {
        // Call the repository method to fetch recent purchases
        return purchaseDao.findRecentPurchases(pageable);
    }}