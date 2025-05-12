package inventorymanagement.stockmodule.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventorymanagement.stockmodule.client.PurchaseFeignClient;
import com.inventorymanagement.stockmodule.client.VendorFeignClient;
import com.inventorymanagement.stockmodule.client.ZoneFeignClient;
import com.inventorymanagement.stockmodule.dao.NotificationRepository;
import com.inventorymanagement.stockmodule.dao.StocksRepository;
import com.inventorymanagement.stockmodule.dto.ItemNameQuantityDto;
import com.inventorymanagement.stockmodule.dto.PurchaseDetailsDto;
import com.inventorymanagement.stockmodule.dto.StockDTO;
import com.inventorymanagement.stockmodule.entity.Notification;
import com.inventorymanagement.stockmodule.entity.Stocks;
import com.inventorymanagement.stockmodule.exception.StockNotFoundException;
import com.inventorymanagement.stockmodule.service.StocksService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocksServiceTest {

    @Mock
    private StocksRepository stockRepository;

    @Mock
    private VendorFeignClient vendorFeignClient;

    @Mock
    private ZoneFeignClient zoneFeignClient;

    @Mock
    private PurchaseFeignClient purchaseFeignClient;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private StocksService stocksService;

    private StockDTO stockDTO;
    private Stocks stockEntity;
    private PurchaseDetailsDto purchaseDetailsDto;
    private UUID stockId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        stockId = UUID.randomUUID();
        now = LocalDateTime.now();
        stockDTO = new StockDTO(stockId, "Test Item", 100, "Electronics", 50.0, "Zone A", "Vendor X", Collections.emptyList(), now, now);
        stockEntity = new Stocks(stockId, "Test Item", 100, "Electronics", 50.0, 1L, "Zone A", 1L, "Vendor X", 1L);
        purchaseDetailsDto = new PurchaseDetailsDto("Vendor X", 150, 45.0, "Electronics", now);
    }

    @Test
    void createStock_newStock_success() {
        when(purchaseFeignClient.getLimitedPurchaseDetails("Test Item")).thenReturn(purchaseDetailsDto);
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.empty());
        when(stockRepository.saveAndFlush(any(Stocks.class))).thenReturn(stockEntity);

        StockDTO createdStock = stocksService.createStock(new StockDTO(null, "Test Item", 0, null, 0.0, "Zone A", null, null, null, null));

        assertNotNull(createdStock.getStockId());
        assertEquals("Test Item", createdStock.getItemName());
        assertEquals(150, createdStock.getQuantity()); // Quantity should come from purchase
        assertEquals("Electronics", createdStock.getCategory());
        assertEquals(45.0 * 150, createdStock.getPrice()); // Price should come from purchase
        assertEquals("Zone A", createdStock.getZoneName());
        assertEquals("Vendor X", createdStock.getVendorName());
        verify(stockRepository, times(1)).findByItemName("Test Item");
        verify(stockRepository, times(1)).saveAndFlush(any(Stocks.class));
    }

    @Test
    void createStock_existingStock_success() {
        LocalDateTime lastRestockedAt = LocalDateTime.now().minusDays(1);
        stockEntity.setLastRestockedAt(lastRestockedAt);
        when(purchaseFeignClient.getLimitedPurchaseDetails("Test Item")).thenReturn(purchaseDetailsDto);
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));
        when(stockRepository.saveAndFlush(any(Stocks.class))).thenReturn(stockEntity);

        StockDTO updatedStock = stocksService.createStock(new StockDTO(null, "Test Item", 0, null, 0.0, "Zone A", null, null, null, null));

        assertEquals(stockEntity.getStockId(), updatedStock.getStockId());
        assertEquals("Test Item", updatedStock.getItemName());
        assertEquals(100 + 150, updatedStock.getQuantity()); // Quantity should be updated
        assertEquals("Electronics", updatedStock.getCategory()); // Category should be updated
        assertEquals(50.0 + (45.0 * 150), updatedStock.getPrice()); // Price should be updated
        assertEquals("Zone A", updatedStock.getZoneName());
        assertEquals("Vendor X", updatedStock.getVendorName());
        assertEquals(purchaseDetailsDto.getPurchaseDate(), updatedStock.getLastRestockedAt());
        verify(stockRepository, times(1)).findByItemName("Test Item");
        verify(stockRepository, times(1)).saveAndFlush(any(Stocks.class));
    }

    @Test
    void createStock_noNewPurchase_throwsIllegalArgumentException() {
        LocalDateTime lastRestockedAt = LocalDateTime.now().plusDays(1);
        stockEntity.setLastRestockedAt(lastRestockedAt);
        when(purchaseFeignClient.getLimitedPurchaseDetails("Test Item")).thenReturn(purchaseDetailsDto);
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));

        assertThrows(IllegalArgumentException.class, () -> stocksService.createStock(new StockDTO(null, "Test Item", 0, null, 0.0, "Zone A", null, null, null, null)));

        verify(stockRepository, times(1)).findByItemName("Test Item");
        verify(stockRepository, never()).saveAndFlush(any(Stocks.class));
    }

    @Test
    void createStock_purchaseDetailsNotFound_throwsIllegalArgumentException() {
        when(purchaseFeignClient.getLimitedPurchaseDetails("Test Item")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> stocksService.createStock(new StockDTO(null, "Test Item", 0, null, 0.0, "Zone A", null, null, null, null)));

        verify(purchaseFeignClient, times(1)).getLimitedPurchaseDetails("Test Item");
        verify(stockRepository, never()).findByItemName(anyString());
        verify(stockRepository, never()).saveAndFlush(any(Stocks.class));
    }

    @Test
    void getAllStocks_success() {
        List<Stocks> stocksList = Arrays.asList(
                stockEntity,
                new Stocks(UUID.randomUUID(), "Item 2", 50, "Books", 10.0, 2L, "Zone B", 2L, "Vendor Y", 2L)
        );
        when(stockRepository.findAll()).thenReturn(stocksList);

        List<StockDTO> allStocks = stocksService.getAllStocks();

        assertEquals(2, allStocks.size());
        assertEquals("Test Item", allStocks.get(0).getItemName());
        assertEquals(100 * 50.0, allStocks.get(0).getPrice()); // Verify total price calculation
        assertEquals("Item 2", allStocks.get(1).getItemName());
        assertEquals(50 * 10.0, allStocks.get(1).getPrice()); // Verify total price calculation
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void getStockById_existingId_success() throws StockNotFoundException {
        when(stockRepository.findById(stockEntity.getStockId())).thenReturn(Optional.of(stockEntity));

        StockDTO foundStock = stocksService.getStockById(stockEntity.getStockId());

        assertEquals(stockDTO.getStockId(), foundStock.getStockId());
        assertEquals(stockDTO.getItemName(), foundStock.getItemName());
        verify(stockRepository, times(1)).findById(stockEntity.getStockId());
    }

    @Test
    void getStockById_nonExistingId_throwsStockNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(stockRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stocksService.getStockById(nonExistingId));
        verify(stockRepository, times(1)).findById(nonExistingId);
    }

    

    

    @Test
    void deleteStockById_existingId_success() throws StockNotFoundException {
        when(stockRepository.existsById(stockId)).thenReturn(true);
        doNothing().when(notificationRepository).deleteByStocks_StockId(stockId);
        doNothing().when(stockRepository).deleteById(stockId);

        stocksService.deleteStockById(stockId);

        verify(stockRepository, times(1)).existsById(stockId);
        verify(notificationRepository, times(1)).deleteByStocks_StockId(stockId);
        verify(stockRepository, times(1)).deleteById(stockId);
    }

    @Test
    void deleteStockById_nonExistingId_throwsStockNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(stockRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(StockNotFoundException.class, () -> stocksService.deleteStockById(nonExistingId));
        verify(stockRepository, times(1)).existsById(nonExistingId);
        verify(notificationRepository, never()).deleteByStocks_StockId(any());
        verify(stockRepository, never()).deleteById(any());
    }

    @Test
    void restockItem_existingId_success() throws StockNotFoundException {
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stockEntity));

        stocksService.restockItem(stockId, 50);

        assertEquals(150, stockEntity.getQuantity());
        verify(stockRepository, times(1)).findById(stockId);
        verify(stockRepository, times(1)).save(stockEntity);
    }

    @Test
    void restockItem_nonExistingId_throwsStockNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(stockRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> stocksService.restockItem(nonExistingId, 50));
        verify(stockRepository, times(1)).findById(nonExistingId);
        verify(stockRepository, never()).save(any(Stocks.class));
    }

    @Test
    void scheduledLowStockCheck_success() {
        Stocks lowStockItem = new Stocks(UUID.randomUUID(), "Low Item", 50, "Misc", 5.0, 3L, "Zone C", 3L, "Vendor Z", 3L);
        Stocks highStockItem = new Stocks(UUID.randomUUID(), "High Item", 200, "Misc", 5.0, 3L, "Zone C", 3L, "Vendor Z", 3L);
        when(stockRepository.findAll()).thenReturn(Arrays.asList(lowStockItem, highStockItem));
        when(notificationRepository.findByStocksAndMessage(lowStockItem, "Stock is running low for item: Low Item")).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification(lowStockItem, "Stock is running low for item: Low Item"));

        List<Notification> notifications = stocksService.scheduledLowStockCheck();

        assertEquals(1, notifications.size());
        assertEquals("Stock is running low for item: Low Item", notifications.get(0).getMessage());
        assertEquals(lowStockItem, notifications.get(0).getStocks());
        verify(stockRepository, times(1)).findAll();
        verify(notificationRepository, times(1)).findByStocksAndMessage(lowStockItem, "Stock is running low for item: Low Item");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void checkAndNotifyLowStock_lowStock_noExistingNotification_createsAndReturnsNotification() {
        when(notificationRepository.findByStocksAndMessage(stockEntity, "Stock is running low for item: Test Item")).thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification(stockEntity, "Stock is running low for item: Test Item"));
        stockEntity.setQuantity(50);

        Notification notification = stocksService.checkAndNotifyLowStock(stockEntity);

        assertNotNull(notification);
        assertEquals("Stock is running low for item: Test Item", notification.getMessage());
        assertEquals(stockEntity, notification.getStocks());
        verify(notificationRepository, times(1)).findByStocksAndMessage(stockEntity, "Stock is running low for item: Test Item");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void checkAndNotifyLowStock_lowStock_existingNotification_returnsExistingNotification() {
        Notification existingNotification = new Notification(stockEntity, "Stock is running low for item: Test Item");
        when(notificationRepository.findByStocksAndMessage(stockEntity, "Stock is running low for item: Test Item")).thenReturn(Optional.of(existingNotification));
        stockEntity.setQuantity(50);

        Notification notification = stocksService.checkAndNotifyLowStock(stockEntity);

        assertEquals(existingNotification, notification);
        verify(notificationRepository, times(1)).findByStocksAndMessage(stockEntity, "Stock is running low for item: Test Item");
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void checkAndNotifyLowStock_sufficientStock_existingLowStockNotification_removesNotificationAndReturnsNull() {
        Notification existingNotification = new Notification(stockEntity, "Stock is running low for item: Test Item");
        when(notificationRepository.findByStocks(stockEntity)).thenReturn(Collections.singletonList(existingNotification));
        stockEntity.setQuantity(150);

        Notification notification = stocksService.checkAndNotifyLowStock(stockEntity);

        assertNull(notification);
        verify(notificationRepository, times(1)).findByStocks(stockEntity);
        verify(notificationRepository, times(1)).delete(existingNotification);
    }

    @Test
    void checkAndNotifyLowStock_sufficientStock_noExistingNotification_returnsNull() {
        when(notificationRepository.findByStocks(stockEntity)).thenReturn(Collections.emptyList());
        stockEntity.setQuantity(150);

        Notification notification = stocksService.checkAndNotifyLowStock(stockEntity);

        assertNull(notification);
        verify(notificationRepository, times(1)).findByStocks(stockEntity);
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    void checkStockAvailability_stockAvailable_returnsTrue() {
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));
        boolean isAvailable = stocksService.checkStockAvailability("Test Item", 50);
        assertTrue(isAvailable);
    }

    @Test
    void checkStockAvailability_stockNotAvailable_returnsFalse() {
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));
        boolean isAvailable = stocksService.checkStockAvailability("Test Item", 150);
        assertFalse(isAvailable);
    }

    @Test
    void checkStockAvailability_stockNotFound_returnsFalse() {
        when(stockRepository.findByItemName("NonExisting Item")).thenReturn(Optional.empty());
        boolean isAvailable = stocksService.checkStockAvailability("NonExisting Item", 50);
        assertFalse(isAvailable);
    }

    @Test
    void updateStockDetails_existingItem_success() {
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));

        stocksService.updateStockDetails("Test Item", 200, 55.0);

        assertEquals(200, stockEntity.getQuantity());
        assertEquals(55.0, stockEntity.getPrice());
        verify(stockRepository, times(1)).findByItemName("Test Item");
        verify(stockRepository, times(1)).saveAndFlush(stockEntity);
    }

    @Test
    void updateStockDetails_nonExistingItem_throwsIllegalArgumentException() {
        when(stockRepository.findByItemName("NonExisting Item")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> stocksService.updateStockDetails("NonExisting Item", 200, 55.0));
        verify(stockRepository, times(1)).findByItemName("NonExisting Item");
        verify(stockRepository, never()).saveAndFlush(any(Stocks.class));
    }

    @Test
    void getAllStockNames_success() {
        List<Stocks> stocksList = Arrays.asList(
                stockEntity,
                new Stocks(UUID.randomUUID(), "Item 2", 50, "Books", 10.0, 2L, "Zone B", 2L, "Vendor Y", 2L)
        );
        when(stockRepository.findAll()).thenReturn(stocksList);

        List<String> stockNames = stocksService.getAllStockNames();

        assertEquals(2, stockNames.size());
        assertTrue(stockNames.contains("Test Item"));
        assertTrue(stockNames.contains("Item 2"));
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void getAvailableItemNamesForStock_success() {
        List<Stocks> stocksList = Collections.singletonList(stockEntity);
        List<String> purchaseItems = Arrays.asList("Test Item", "New Item", "Another New Item");
        when(stockRepository.findAll()).thenReturn(stocksList);
        when(purchaseFeignClient.getItemNames()).thenReturn(purchaseItems);

        List<String> availableItems = stocksService.getAvailableItemNamesForStock();

        assertEquals(2, availableItems.size());
        assertTrue(availableItems.contains("New Item"));
        assertTrue(availableItems.contains("Another New Item"));
        assertFalse(availableItems.contains("Test Item"));
        verify(stockRepository, times(1)).findAll();
        verify(purchaseFeignClient, times(1)).getItemNames();
    }

    @Test
    void getVendorNames_success() {
        List<String> vendorNames = Arrays.asList("Vendor X", "Vendor Y", "Vendor Z");
        when(vendorFeignClient.getVendorNames()).thenReturn(vendorNames);

        List<String> fetchedVendorNames = stocksService.getVendorNames();

        assertEquals(3, fetchedVendorNames.size());
        assertTrue(fetchedVendorNames.contains("Vendor X"));
        assertTrue(fetchedVendorNames.contains("Vendor Y"));
        assertTrue(fetchedVendorNames.contains("Vendor Z"));
        verify(vendorFeignClient, times(1)).getVendorNames();
    }

    @Test
    void getNamesOfActiveZones_success() {
        List<String> zoneNames = Arrays.asList("Zone A", "Zone B", "Zone C");
        when(zoneFeignClient.getNamesOfActiveZones()).thenReturn(zoneNames);

        List<String> fetchedZoneNames = stocksService.getNamesOfActiveZones();

        assertEquals(3, fetchedZoneNames.size());
        assertTrue(fetchedZoneNames.contains("Zone A"));
        assertTrue(fetchedZoneNames.contains("Zone B"));
        assertTrue(fetchedZoneNames.contains("Zone C"));
        verify(zoneFeignClient, times(1)).getNamesOfActiveZones();
    }

    @Test
    void getItemNames_success() {
        List<String> itemNames = Arrays.asList("Item 1", "Item 2", "Item 3");
        when(purchaseFeignClient.getItemNames()).thenReturn(itemNames);

        List<String> fetchedItemNames = stocksService.getItemNames();

        assertEquals(3, fetchedItemNames.size());
        assertTrue(fetchedItemNames.contains("Item 1"));
        assertTrue(fetchedItemNames.contains("Item 2"));
        assertTrue(fetchedItemNames.contains("Item 3"));
        verify(purchaseFeignClient, times(1)).getItemNames();
    }

    @Test
    void getItemNameAndQuantity_success() {
        List<Object[]> results = Arrays.asList(new Object[]{"Test Item", 100}, new Object[]{"Item 2", 50});
        when(stockRepository.findItemNameAndQuantity()).thenReturn(results);

        List<ItemNameQuantityDto> metrics = stocksService.getItemNameAndQuantity();

        assertEquals(2, metrics.size());
        assertEquals("Test Item", metrics.get(0).getItemName());
        assertEquals(100, metrics.get(0).getQuantity());
        assertEquals("Item 2", metrics.get(1).getItemName());
        assertEquals(50, metrics.get(1).getQuantity());
        verify(stockRepository, times(1)).findItemNameAndQuantity();
    }

    @Test
    void getStockDetails_existingItem_success() {
        when(stockRepository.findByItemName("Test Item")).thenReturn(Optional.of(stockEntity));

        StockDTO details = stocksService.getStockDetails("Test Item");

        assertEquals("Test Item", details.getItemName());
        assertEquals(100, details.getQuantity());
        assertEquals(50.0, details.getPrice());
        verify(stockRepository, times(1)).findByItemName("Test Item");
    }

    @Test
    void getStockDetails_nonExistingItem_throwsIllegalArgumentException() {
        when(stockRepository.findByItemName("NonExisting Item")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> stocksService.getStockDetails("NonExisting Item"));
        verify(stockRepository, times(1)).findByItemName("NonExisting Item");
    }
}