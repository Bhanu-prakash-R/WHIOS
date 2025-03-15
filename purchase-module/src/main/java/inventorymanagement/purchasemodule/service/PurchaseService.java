package inventorymanagement.purchasemodule.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inventorymanagement.purchasemodule.dao.PurchaseDao;
import inventorymanagement.purchasemodule.dto.PurchaseDto;
import inventorymanagement.purchasemodule.entity.Purchase;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    public List<PurchaseDto> getAllPurchases() {
        return purchaseDao.findAll().stream()
                          .map(this::convertToDto)
                          .collect(Collectors.toList());
    }

    public Optional<PurchaseDto> getPurchaseById(UUID id) {
        return purchaseDao.findById(id).map(this::convertToDto);
    }

    public PurchaseDto savePurchase(PurchaseDto purchaseDto) {
        Purchase purchase = convertToEntity(purchaseDto);
        Purchase savedPurchase = purchaseDao.save(purchase);
        return convertToDto(savedPurchase);
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
}