package inventorymanagement.stockmodule.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import inventorymanagement.stockmodule.dao.StocksRepository;
import inventorymanagement.stockmodule.Client.PurchaseFeignClient;
import inventorymanagement.stockmodule.Client.VendorFeignClient;
import inventorymanagement.stockmodule.Client.ZoneFeignClient;
import inventorymanagement.stockmodule.dao.NotificationRepository;
import inventorymanagement.stockmodule.dto.ItemNameQuantityDto;
import inventorymanagement.stockmodule.dto.StockDTO;
import inventorymanagement.stockmodule.entity.Notification;
import inventorymanagement.stockmodule.entity.Stocks;
import inventorymanagement.stockmodule.exception.StockNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StocksService {

    @Autowired
    private StocksRepository stockRepository;
    
    @Autowired
    private VendorFeignClient vendorFeignClient;
    
    @Autowired
    private ZoneFeignClient zoneFeignClient;
    
    @Autowired
    private PurchaseFeignClient purchaseFeignClient;

    @Autowired
    private NotificationRepository notificationRepository;
    private static final int LOW_STOCK_THRESHOLD = 100;

    // Convert Stocks entity to StockDTO
    private StockDTO convertToDTO(Stocks stock) {
    	
    	
            log.debug("Converting Stocks entity to StockDTO. Stock ID: {}, Item Name: {}", stock.getStockId(), stock.getItemName());

            // Fetch notifications for the stock
            List<String> notificationMessages = notificationRepository.findByStocks(stock)
                    .stream().map(Notification::getMessage).collect(Collectors.toList());

            log.debug("Notification messages for Stock ID: {}: {}", stock.getStockId(), notificationMessages);
        return new StockDTO(
            stock.getStockId(),
            stock.getItemName(),
            stock.getQuantity(),
            stock.getCategory(),
            stock.getPrice(),
            stock.getZoneName(),
            stock.getVendorName(),
            notificationMessages,
            stock.getCreatedAt()
        );
    }


    // Convert StockDTO to Stocks entity
    private Stocks convertToEntity(StockDTO stockDTO) {
        UUID stockId = stockDTO.getStockId() != null ? stockDTO.getStockId() : UUID.randomUUID();
        return new Stocks(
            stockId,
            stockDTO.getItemName(),
            stockDTO.getQuantity(),
            stockDTO.getCategory(),
            stockDTO.getPrice(),
            0, // Default zoneId
            stockDTO.getZoneName(),
            0, // Default vendorId
            stockDTO.getVendorName(),
   //         stockDTO.getCreatedAt(),
            0 // Default value for purchaseId
        );
    }

    // 1. Create Stock
    @Transactional
    public StockDTO createStock(StockDTO stockDTO) {
        log.info("Creating or updating stock with itemName: {}", stockDTO.getItemName());

        // Check if the item already exists in stock
        Optional<Stocks> existingStock = stockRepository.findByItemName(stockDTO.getItemName());

        Stocks stock;
        if (existingStock.isPresent()) {
            // If stock exists, increment the quantity
            stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + stockDTO.getQuantity()); // Add new quantity to existing
            log.info("Stock updated for itemName: {}. New quantity: {}", stock.getItemName(), stock.getQuantity());
        } else {
            // If stock does not exist, create a new stock entry
            stock = convertToEntity(stockDTO);
            log.info("New stock entry created for itemName: {} with quantity: {}", stock.getItemName(), stock.getQuantity());
        }

        // Save the stock and flush to ensure persistence
        stock = stockRepository.save(stock);
        stockRepository.saveAndFlush(stock);

        // Convert the stock entity to a DTO
        StockDTO responseDTO = convertToDTO(stock);

        // Replace the price field with the total price (price * quantity)
        responseDTO.setPrice(stock.getPrice() * stock.getQuantity());

        log.info("Created or updated stock with total price: {}", responseDTO.getPrice());
        return responseDTO;
    }


    // 2. Display All Stocks
    public List<StockDTO> getAllStocks() {
        log.info("Entering getAllStocks method");
        List<StockDTO> stockDTOs = new CopyOnWriteArrayList<>();
        try {
            List<Stocks> stocks = stockRepository.findAll();
            for (Stocks stock : stocks) {
                synchronized (stock) {
                    stockDTOs.add(convertToDTO(stock));
                    log.info("Initialized and added stock with ID: {}", stock.getStockId());
                }
            }
            log.info("Exiting getAllStocks method");
        } catch (Exception e) {
            log.error("Error fetching all stocks: ", e);
            throw e;
        }
        return stockDTOs;
    }

    // 3. Get Stock by ID
    @Transactional
    public StockDTO getStockById(UUID stockId) throws StockNotFoundException {
        log.info("Fetching stock by ID: {}", stockId);
        Stocks stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for ID: " + stockId));
        return convertToDTO(stock);
    }

    // 4. Update Stock
    @Transactional
    public void updateStock(String stockIdString, StockDTO stockDTO) throws StockNotFoundException {
        log.info("Updating stock with ID: {}", stockIdString);
        UUID stockId = UUID.fromString(stockIdString);
        Stocks stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for ID: " + stockId));
        if (stockDTO.getItemName() != null && !stockDTO.getItemName().isEmpty()) {
            stock.setItemName(stockDTO.getItemName());
        }
        if (stockDTO.getQuantity() >= 0) {
            stock.setQuantity(stockDTO.getQuantity());
        }
        if (stockDTO.getPrice() >= 0) {
            stock.setPrice(stockDTO.getPrice());
        }
        if (stockDTO.getCategory() != null && !stockDTO.getCategory().isEmpty()) {
            stock.setCategory(stockDTO.getCategory());
        }
        stock.setZoneName(stockDTO.getZoneName());
        stock.setVendorName(stockDTO.getVendorName());
        stockRepository.save(stock);
    }

    // 5. Remove Stock
    @Transactional
    public void removeItem(UUID stockId, int quantity) throws StockNotFoundException {
        log.info("Removing item with stock ID: {} by quantity: {}", stockId, quantity);
        Stocks stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for ID: " + stockId));
        if (stock.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for item: " + stockId);
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);
    }

    // 6. Restock Item
    @Transactional
    public void restockItem(UUID stockId, int quantity) throws StockNotFoundException {
        log.info("Restocking item with stock ID: {} by quantity: {}", stockId, quantity);
        Stocks stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for ID: " + stockId));
        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);
    }

    // 7. Check and Notify Low Stock
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledLowStockCheck() {
        log.info("Scheduled task to check and notify low stock started.");
        checkAndNotifyLowStock();
        log.info("Scheduled task to check and notify low stock completed.");
    }

    @Transactional
    public void checkAndNotifyLowStock() {
        log.info("Checking for low stock and sending notifications.");
        List<Stocks> stocksList = stockRepository.findByQuantityLessThan(LOW_STOCK_THRESHOLD);  // Use the custom query
        log.debug("Total stocks to check: {}", stocksList.size());

        for (Stocks stock : stocksList) {
            log.debug("Checking stock: {}", stock.getItemName());
            log.debug("Current quantity: {}", stock.getQuantity());
            
            List<Notification> existingNotifications = notificationRepository.findByStocks(stock);  // <-- Added this line
            boolean notificationExists = existingNotifications.stream()  // <-- Added this line
                    .anyMatch(notification -> notification.getMessage().contains("Stock is running low for item: "));  // <-- Added this line
            log.debug("Existing notifications for stock {}: {}", stock.getItemName(), existingNotifications);
            if (stock.getQuantity() < LOW_STOCK_THRESHOLD && !notificationExists) {
                log.info("Stock is low for item: {}", stock.getItemName());
                Notification notification = new Notification(stock, "Stock is running low for item: " + stock.getItemName());

                // Save notification
               

                log.debug("Saving stock: {}", stock);
                stockRepository.save(stock);
                log.debug("Stock saved with ID: {}", stock.getStockId());
                
                log.debug("Saving notification: {}", notification);
                notificationRepository.save(notification);
                log.debug("Notification saved.");

                log.info("Saved Notification for item: {} with message: {}", stock.getItemName(), notification.getMessage());
            } else {
            	if (notificationExists) {  // <-- Added this condition
                    log.debug("Notification already exists for item: {}", stock.getItemName());
                }
            	if (stock.getQuantity() >= LOW_STOCK_THRESHOLD) {  // <-- Added this condition
                    log.debug("Stock quantity for item {} is sufficient.", stock.getItemName());
                }
              
            }
        }
        log.info("Completed checking for low stock and sending notifications.");
    }
    
    @Transactional
    public boolean checkStockAvailability(String itemName, int quantity) {
        Optional<Stocks> stockItemOptional = stockRepository.findByItemName(itemName);
        return stockItemOptional.map(stockItem -> stockItem.getQuantity() >= quantity).orElse(false);
    }

    // 8. Update Stock Quantity
    @Transactional
    public void updateStockQuantity(String itemName, int quantity) {
        Optional<Stocks> stockItemOptional = stockRepository.findByItemName(itemName);
        if (stockItemOptional.isPresent()) {
            Stocks stockItem = stockItemOptional.get();
            if (stockItem.getQuantity() >= quantity) {
                stockItem.setQuantity(stockItem.getQuantity() - quantity);
                stockRepository.save(stockItem);
            } else {
                throw new RuntimeException("Insufficient stock for item: " + itemName);
            }
        } else {
            throw new RuntimeException("Stock item not found: " + itemName);
        }
    }
    // 8. Get All Stock Names
    public List<String> getAllStockNames() {
        log.info("Fetching all stock names");
        List<String> stockNames = new CopyOnWriteArrayList<>();
        List<Stocks> stocks = stockRepository.findAll();
        for (Stocks stock : stocks) {
            synchronized (stock) {
                stockNames.add(stock.getItemName());
            }
        }
        log.info("Fetched stock names: {}", stockNames);
        return stockNames;
    }
    
    public List<String> getAvailableItemNamesForStock() {
        log.info("Fetching item names not already in stock...");

        // Item names already in stock
        List<String> stockItemNames = stockRepository.findAll().stream()
                .map(Stocks::getItemName)
                .distinct()
                .collect(Collectors.toList());

        // Item names from Purchase records
        List<String> purchaseItemNames = purchaseFeignClient.getItemNames();

        // Exclude items already in stock
        List<String> availableItemNames = purchaseItemNames.stream()
                .filter(itemName -> !stockItemNames.contains(itemName))
                .collect(Collectors.toList());

        log.info("Available item names for stock addition: {}", availableItemNames);
        return availableItemNames;
    }

    public List<String> getVendorNames(){
    	log.info("Fetching all vendor names");
    	return vendorFeignClient.getVendorNames();
    }
    
    public List<String> getNamesOfActiveZones(){
    	log.info("Fetching Active zone Names");
    	return zoneFeignClient.getNamesOfActiveZones();
    			
    }
    public List<String> getItemNames(){
    	log.info("fetching itemNames from purchase");
    	return purchaseFeignClient.getItemNames();
    }
    
    public List<ItemNameQuantityDto> getItemNameAndQuantity() {
        log.info("Fetching item name and quantity from the stock database");

        List<Object[]> results = stockRepository.findItemNameAndQuantity();
        log.info("Fetched {} records", results.size());

        // Convert query results to ItemQuantityDTO
        List<ItemNameQuantityDto> stockMetrics = results.stream()
                .map(result -> new ItemNameQuantityDto((String) result[0], ((Number) result[1]).intValue()))
                .collect(Collectors.toList());
        log.info("Successfully transformed stock data into ItemQuantityDTO list");

        return stockMetrics;
    }

    /*public List<Map<String, Object>> getGraphMetrics() {
        List<Object[]> data = stockRepository.findItemNameAndQuantity();
        List<Map<String, Object>> graphData = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("itemName", row[0]);
            entry.put("quantity", row[1]);
            graphData.add(entry);
        }
        return graphData;
    }*/

    
}
