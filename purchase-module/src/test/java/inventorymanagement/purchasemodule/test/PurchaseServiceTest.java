package inventorymanagement.purchasemodule.test;

import inventorymanagement.purchasemodule.Client.VendorFeignClient;
import inventorymanagement.purchasemodule.dao.PurchaseDao;
import inventorymanagement.purchasemodule.dto.PurchaseDetailsDto;
import inventorymanagement.purchasemodule.dto.PurchaseDto;
import inventorymanagement.purchasemodule.dto.PurchaseMetricsDto;
import inventorymanagement.purchasemodule.dto.VendorResponseDto;
import inventorymanagement.purchasemodule.entity.Purchase;
import inventorymanagement.purchasemodule.service.PurchaseService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    private PurchaseDao purchaseDao;

    @Mock
    private VendorFeignClient vendorFeignClient;

    @Mock
    private Logger log; // Mock the logger

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void getAllPurchases_success() {
        // Arrange
        List<Purchase> mockPurchases = Arrays.asList(
                createPurchaseEntity(UUID.randomUUID(), 10, 25.0, LocalDateTime.now(), UUID.randomUUID(), "Electronics", "VendorA", "Laptop"),
                createPurchaseEntity(UUID.randomUUID(), 5, 10.0, LocalDateTime.now().minusDays(1), UUID.randomUUID(), "Books", "VendorB", "Novel")
        );
        when(purchaseDao.findAll()).thenReturn(mockPurchases);

        // Act
        List<PurchaseDto> purchases = purchaseService.getAllPurchases();

        // Assert
        assertEquals(mockPurchases.size(), purchases.size());
        assertEquals(mockPurchases.get(0).getItemName(), purchases.get(0).getItemName());
        assertEquals(mockPurchases.get(1).getVendorName(), purchases.get(1).getVendorName());
        verify(purchaseDao, times(1)).findAll();
    }

    @Test
    void getPurchaseById_success() {
        // Arrange
        UUID purchaseId = UUID.randomUUID();
        Purchase mockPurchase = createPurchaseEntity(purchaseId, 10, 25.0, LocalDateTime.now(), UUID.randomUUID(), "Electronics", "VendorA", "Laptop");
        when(purchaseDao.findById(purchaseId)).thenReturn(Optional.of(mockPurchase));

        // Act
        Optional<PurchaseDto> purchaseDto = purchaseService.getPurchaseById(purchaseId);

        // Assert
        assertTrue(purchaseDto.isPresent());
        assertEquals(mockPurchase.getPurchaseId(), purchaseDto.get().getPurchaseId());
        assertEquals(mockPurchase.getItemName(), purchaseDto.get().getItemName());
        verify(purchaseDao, times(1)).findById(purchaseId);
    }

    @Test
    void getPurchaseById_notFound() {
        // Arrange
        UUID purchaseId = UUID.randomUUID();
        when(purchaseDao.findById(purchaseId)).thenReturn(Optional.empty());

        // Act
        Optional<PurchaseDto> purchaseDto = purchaseService.getPurchaseById(purchaseId);

        // Assert
        assertFalse(purchaseDto.isPresent());
        verify(purchaseDao, times(1)).findById(purchaseId);
    }

    @Test
    void savePurchase_success() {
        // Arrange
        UUID vendorId = UUID.randomUUID();
        PurchaseDto purchaseDto = createPurchaseDto(null, 5, 10.0, LocalDateTime.now(), vendorId, "Books", "VendorB", "Novel");
        Purchase purchaseEntityToSave = convertDtoToEntity(purchaseDto);
        Purchase savedPurchaseEntity = createPurchaseEntity(UUID.randomUUID(), 5, 10.0, LocalDateTime.now(), vendorId, "Books", "VendorB", "Novel");
        VendorResponseDto vendorResponseDto = new VendorResponseDto(vendorId, "VendorB");
        List<VendorResponseDto> vendors = Collections.singletonList(vendorResponseDto);

        when(vendorFeignClient.getAllVendors()).thenReturn(vendors);
        when(purchaseDao.save(any(Purchase.class))).thenReturn(savedPurchaseEntity);

        // Act
        PurchaseDto savedPurchaseDto = purchaseService.savePurchase(purchaseDto);

        // Assert
        assertNotNull(savedPurchaseDto.getPurchaseId());
        assertEquals(purchaseDto.getQuantity(), savedPurchaseDto.getQuantity());
        assertEquals(purchaseDto.getPrice() * purchaseDto.getQuantity(), savedPurchaseDto.getPrice()); // Total price check
        assertEquals(purchaseDto.getItemName(), savedPurchaseDto.getItemName());
        assertEquals(purchaseDto.getVendorId(), savedPurchaseDto.getVendorId());
        verify(vendorFeignClient, times(1)).getAllVendors();
        verify(purchaseDao, times(1)).save(any(Purchase.class));
    }

    @Test
    void savePurchase_invalidVendor() {
        // Arrange
        UUID vendorId = UUID.randomUUID();
        PurchaseDto purchaseDto = createPurchaseDto(null, 5, 10.0, LocalDateTime.now(), vendorId, "Books", "Invalid Vendor", "Novel");
        List<VendorResponseDto> vendors = Collections.singletonList(new VendorResponseDto(UUID.randomUUID(), "Another Vendor"));

        when(vendorFeignClient.getAllVendors()).thenReturn(vendors);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> purchaseService.savePurchase(purchaseDto));
        assertEquals("Invalid vendor: Vendor ID '" + purchaseDto.getVendorId() + "' and Vendor Name '" + purchaseDto.getVendorName() + "' do not match.", exception.getMessage());
        verify(vendorFeignClient, times(1)).getAllVendors();
        verify(purchaseDao, never()).save(any(Purchase.class));
    }

    @Test
    void deletePurchase_success() {
        // Arrange
        UUID purchaseId = UUID.randomUUID();
        doNothing().when(purchaseDao).deleteById(purchaseId);

        // Act
        purchaseService.deletePurchase(purchaseId);

        // Assert
        verify(purchaseDao, times(1)).deleteById(purchaseId);
    }

    @Test
    void getAllVendors_success() {
        // Arrange
        List<VendorResponseDto> mockVendors = Arrays.asList(
                new VendorResponseDto(UUID.randomUUID(), "VendorX"),
                new VendorResponseDto(UUID.randomUUID(), "VendorY")
        );
        when(vendorFeignClient.getAllVendors()).thenReturn(mockVendors);

        // Act
        List<VendorResponseDto> vendors = purchaseService.getAllVendors();

        // Assert
        assertEquals(mockVendors.size(), vendors.size());
        assertEquals(mockVendors.get(0).getVendorName(), vendors.get(0).getVendorName());
        verify(vendorFeignClient, times(1)).getAllVendors();
    }

    @Test
    void getItemNames_success() {
        // Arrange
        List<Purchase> mockPurchases = Arrays.asList(
                createPurchaseEntity(UUID.randomUUID(), 10, 25.0, LocalDateTime.now(), UUID.randomUUID(), "Electronics", "VendorA", "Laptop"),
                createPurchaseEntity(UUID.randomUUID(), 5, 10.0, LocalDateTime.now().minusDays(1), UUID.randomUUID(), "Books", "VendorB", "Novel"),
                createPurchaseEntity(UUID.randomUUID(), 2, 50.0, LocalDateTime.now().minusDays(2), UUID.randomUUID(), "Electronics", "VendorC", "Laptop")
        );
        when(purchaseDao.findAll()).thenReturn(mockPurchases);

        // Act
        List<String> itemNames = purchaseService.getItemNames();

        // Assert
        assertEquals(2, itemNames.size());
        assertTrue(itemNames.contains("Laptop"));
        assertTrue(itemNames.contains("Novel"));
        verify(purchaseDao, times(1)).findAll();
    }

    @Test
    void getVendorNames_success() {
        // Arrange
        List<String> mockVendorNames = Arrays.asList("VendorP", "VendorQ", "VendorP");
        when(vendorFeignClient.getVendorNames()).thenReturn(mockVendorNames);

        // Act
        List<String> vendorNames = purchaseService.getVendorNames();

        // Assert
        assertEquals(mockVendorNames.size(), vendorNames.size());
        assertTrue(vendorNames.contains("VendorP"));
        assertTrue(vendorNames.contains("VendorQ"));
        verify(vendorFeignClient, times(1)).getVendorNames();
    }


    @Test
    void getLimitedPurchaseDetailsByItemName_success() {
        // Arrange
        String itemName = "TestItem";
        List<Purchase> mockPurchases = Arrays.asList(
                createPurchaseEntity(UUID.randomUUID(), 5, 10.0, LocalDateTime.now().minusDays(2), UUID.randomUUID(), "CategoryA", "VendorX", itemName),
                createPurchaseEntity(UUID.randomUUID(), 10, 12.5, LocalDateTime.now(), UUID.randomUUID(), "CategoryB", "VendorY", itemName),
                createPurchaseEntity(UUID.randomUUID(), 2, 15.0, LocalDateTime.now().minusDays(1), UUID.randomUUID(), "CategoryA", "VendorZ", itemName)
        );
        when(purchaseDao.findByItemName(itemName)).thenReturn(mockPurchases);

        // Act
        PurchaseDetailsDto purchaseDetailsDto = purchaseService.getLimitedPurchaseDetailsByItemName(itemName);

        // Assert
        assertNotNull(purchaseDetailsDto);
        assertEquals("VendorY", purchaseDetailsDto.getVendorName());
        assertEquals(10, purchaseDetailsDto.getQuantity());
        assertEquals(12.5, purchaseDetailsDto.getPrice());
        assertEquals("CategoryB", purchaseDetailsDto.getCategory());
        assertEquals(LocalDateTime.now().toLocalDate(), purchaseDetailsDto.getPurchaseDate().toLocalDate()); // Compare only date part
        verify(purchaseDao, times(1)).findByItemName(itemName);
    }

    @Test
    void getLimitedPurchaseDetailsByItemName_noPurchasesFound() {
        // Arrange
        String itemName = "NonExistingItem";
        when(purchaseDao.findByItemName(itemName)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.getLimitedPurchaseDetailsByItemName(itemName));
        assertEquals("No purchase found for item: " + itemName, exception.getMessage());
        verify(purchaseDao, times(1)).findByItemName(itemName);
    }

    private Purchase createPurchaseEntity(UUID purchaseId, int quantity, double price, LocalDateTime purchaseDate, UUID vendorId, String category, String vendorName, String itemName) {
        Purchase purchase = new Purchase();
        purchase.setPurchaseId(purchaseId);
        purchase.setQuantity(quantity);
        purchase.setPrice(price);
        purchase.setPurchaseDate(purchaseDate);
        purchase.setVendorId(vendorId);
        purchase.setCategory(category);
        purchase.setVendorName(vendorName);
        purchase.setItemName(itemName);
        return purchase;
    }

    private PurchaseDto createPurchaseDto(UUID purchaseId, int quantity, double price, LocalDateTime purchaseDate, UUID vendorId, String category, String vendorName, String itemName) {
        return new PurchaseDto(purchaseId, quantity, vendorId, price, purchaseDate, category, vendorName, itemName);
    }

    private Purchase convertDtoToEntity(PurchaseDto dto) {
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