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
import inventorymanagement.stockmodule.dto.PurchaseDetailsDto;
import inventorymanagement.stockmodule.dto.StockDTO;
import inventorymanagement.stockmodule.entity.Notification;
import inventorymanagement.stockmodule.entity.Stocks;
import inventorymanagement.stockmodule.exception.StockNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Service class for managing stocks.
 * Includes logging, database operations, and external service interactions.
 * Autowires repositories and Feign clients for seamless integration.
 */
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
    /**
     * Converts a Stocks entity into a StockDTO.
     * Includes logging for debugging and maps entity fields to DTO fields.
     * Fetches related notification messages for the stock.
     *
     * @param stock The Stocks entity to be converted.
     * @return StockDTO object containing the mapped fields.
     */
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
    /**
     * Converts a StockDTO object into a Stocks entity.
     * Generates a new UUID if stockId is null and sets default values for zoneId, vendorId, and purchaseId.
     *
     * @param stockDTO The StockDTO object to be converted.
     * @return Stocks entity with mapped and default fields.
     */
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
    /**
     * Creates or updates a stock entry.
     * Updates quantity if stock exists, otherwise creates a new entry.
     * Saves stock data, calculates total price, and returns the updated details.
     */
    @Transactional
    public StockDTO createStock(StockDTO stockDTO) {
        log.info("Creating or updating stock for itemName: {}, zoneName: {}", stockDTO.getItemName(), stockDTO.getZoneName());

        // Validate item exists in the Purchase module
        log.info("Validating if item '{}' exists in Purchase module...", stockDTO.getItemName());
        List<String> purchasedItemNames = purchaseFeignClient.getItemNames();

        if (!purchasedItemNames.contains(stockDTO.getItemName())) {
            log.error("Item '{}' not found in Purchase module. Cannot add to stock.", stockDTO.getItemName());
            throw new IllegalArgumentException("Item not found in Purchase module: " + stockDTO.getItemName());
        }

        // Fetch purchase details for autofill
        PurchaseDetailsDto purchaseDetails;
        try {
            log.info("Fetching purchase details for itemName: {}", stockDTO.getItemName());
            purchaseDetails = purchaseFeignClient.getLimitedPurchaseDetails(stockDTO.getItemName());
            if (purchaseDetails == null) {
                log.error("No purchase details found for itemName: {}", stockDTO.getItemName());
                throw new IllegalArgumentException("Purchase details not available for the given item name.");
            }
        } catch (Exception e) {
            log.error("Error fetching purchase details for item '{}': {}", stockDTO.getItemName(), e.getMessage());
            throw new IllegalArgumentException("Unable to fetch purchase details for item: " + stockDTO.getItemName());
        }

        // Autofill stockDTO fields with data from the Purchase module
        stockDTO.setVendorName(purchaseDetails.getVendorName());
        stockDTO.setCategory(purchaseDetails.getCategory());
        stockDTO.setQuantity(purchaseDetails.getQuantity());
        stockDTO.setPrice(purchaseDetails.getPrice());

        // Fetch all matching stock entries
        log.info("Checking if item '{}' already exists in stock...", stockDTO.getItemName());
        Optional<Stocks> stocksList = stockRepository.findByItemName(stockDTO.getItemName());

        Stocks stock;
        if (stocksList.isEmpty()) {
            // Create new stock entry if no match found
            stock = convertToEntity(stockDTO);
            log.info("New stock entry created for itemName: {} with quantity: {}", stock.getItemName(), stock.getQuantity());
        } else {
            // Handle multiple results by selecting the first one (or custom logic)
            stock = stocksList.get(); // Use the first record for simplicity
            stock.setQuantity(stock.getQuantity() + stockDTO.getQuantity()); // Update quantity
            stock.setPrice(stock.getPrice() + (purchaseDetails.getPrice() * purchaseDetails.getQuantity())); // Update total price
            stock.setVendorName(purchaseDetails.getVendorName());
            stock.setCategory(purchaseDetails.getCategory());
            log.info("Stock updated for itemName: {}. New quantity: {}, New price: {}", stock.getItemName(), stock.getQuantity(), stock.getPrice());
        }

        // Save the stock and flush to ensure persistence
        stock = stockRepository.saveAndFlush(stock);

        // Convert the stock entity to a DTO for the response
        StockDTO responseDTO = convertToDTO(stock);
        responseDTO.setPrice(stock.getPrice()); // Total price
        responseDTO.setQuantity(stock.getQuantity());
        log.info("Stock created/updated successfully for itemName: {} with total price: {}", stockDTO.getItemName(), responseDTO.getPrice());

        return responseDTO;
    }

    // 2. Display All Stocks
    /**
     * Fetches and returns a list of all stock entries as StockDTO objects.
     * Handles database interaction, ensures thread safety, and logs operations.
     *
     * @return List of StockDTO objects representing all stocks in the database.
     */
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
    /**
     * Retrieves a stock by its unique ID.
     * Throws StockNotFoundException if no stock is found for the given ID.
     * Logs the operation and converts the stock entity to a StockDTO.
     *
     * @param stockId The unique identifier of the stock.
     * @return StockDTO representing the requested stock.
     * @throws StockNotFoundException If the stock is not found in the database.
     */
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
        checkAndNotifyLowStock(stock);
    }

    // 5. Remove Stock
    /**
    * Updates an existing stock entry by its ID.
    * Validates fields from the provided StockDTO and updates non-null, valid values.
    * Throws StockNotFoundException if the stock ID is not found.
    * Logs the operation and saves the updated stock to the database.
    *
    * @param stockIdString The ID of the stock to be updated (as a string).
    * @param stockDTO The DTO containing the updated stock details.
    * @throws StockNotFoundException If no stock is found for the given ID.
    */
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
    /**
     * Restocks an item by increasing its quantity.
     * Throws StockNotFoundException if the stock ID is not found.
     * Logs the operation and updates the stock in the database.
     *
     * @param stockId The unique identifier of the stock to restock.
     * @param quantity The amount to add to the current stock quantity.
     * @throws StockNotFoundException If the stock is not found in the database.
     */
    @Transactional
    public void restockItem(UUID stockId, int quantity) throws StockNotFoundException {
        log.info("Restocking item with stock ID: {} by quantity: {}", stockId, quantity);
        Stocks stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for ID: " + stockId));
        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);
    }

    // 7. Check and Notify Low Stock
    /**
     * Scheduled task that runs daily at midnight to check and notify low stock levels.
     * Logs the start and completion of the task while invoking the low stock check method.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public List<Notification> scheduledLowStockCheck() {
        log.info("Scheduled task to check and notify low stock started.");
        List<Notification> lowStockNotifications = new ArrayList<>();
        List<Stocks> stocksList = stockRepository.findAll(); // Fetch all stocks
        for (Stocks stock : stocksList) {
            Notification notification = checkAndNotifyLowStock(stock); // Call the method with each stock
            if (notification != null) {
                lowStockNotifications.add(notification);
            }
        }
        log.info("Scheduled task to check and notify low stock completed.");
        return lowStockNotifications;
    }
 
    @PostConstruct
    public void init() {
        log.debug("NotificationRepository: {}", notificationRepository);
    }

    /**
     * Checks for low stock items and sends notifications if needed.
     * - Identifies stocks with quantities below the threshold.
     * - Ensures no duplicate notifications for the same item.
     * - Logs and saves updated stock and notifications to the database.
     */
    @Transactional
    public Notification checkAndNotifyLowStock(Stocks stock) {
        log.info("Checking stock: {} with quantity: {}", stock.getItemName(), stock.getQuantity());
 
        if (stock.getQuantity() < LOW_STOCK_THRESHOLD) {
            Optional<Notification> existingNotification = notificationRepository.findByStocksAndMessage(stock, "Stock is running low for item: " + stock.getItemName());
            if (existingNotification.isEmpty()) {
                log.info("Stock is low for item: {}", stock.getItemName());
                Notification notification = new Notification(stock, "Stock is running low for item: " + stock.getItemName());
                notificationRepository.save(notification);
                log.info("Notification saved for item: {}", stock.getItemName());
                return notification; // Return the notification
            } else {
                log.info("Notification already exists for item: {}", stock.getItemName());
                return existingNotification.get(); // Return the existing notification
            }
        } else {
            List<Notification> notifications = notificationRepository.findByStocks(stock);
            for (Notification notification : notifications) {
                if (notification.getMessage().contains("Stock is running low for item: " + stock.getItemName())) {
                    log.info("Removing notification for item: {}", stock.getItemName());
                    notificationRepository.delete(notification);
                    log.info("Notification removed for item: {}", stock.getItemName());
                }
            }
            return null; // No low stock notification
        }
    }
    /**
    * Checks if a specified quantity of a stock item is available.
    * Queries the stock by item name and verifies if the quantity meets or exceeds the required amount.
    *
    * @param itemName The name of the stock item to check.
    * @param quantity The quantity required.
    * @return true if the stock is available in sufficient quantity, otherwise false.
    */
    @Transactional
    public boolean checkStockAvailability(String itemName, int quantity) {
        Optional<Stocks> stockItemOptional = stockRepository.findByItemName(itemName);
        return stockItemOptional.map(stockItem -> stockItem.getQuantity() >= quantity).orElse(false);
    }

    // 8. Update Stock Quantity
    /**
     * Updates the stock quantity for a specified item.
     * - Reduces the quantity by the specified amount if sufficient stock is available.
     * - Throws a RuntimeException if stock is insufficient or the item is not found.
     *
     * @param itemName The name of the stock item to update.
     * @param quantity The quantity to reduce from the stock.
     */
    
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
    /**
     * Provides utility methods for retrieving stock, vendor, zone, and purchase details:
     * - getAllStockNames: Returns a list of all stock item names from the database.
     * - getAvailableItemNamesForStock: Finds item names not currently in stock by comparing them with purchase records.
     * - getVendorNames: Fetches all vendor names from an external service.
     * - getNamesOfActiveZones: Retrieves active zone names from an external service.
     * - getItemNames: Fetches item names from purchase records via an external service.
     * - getItemNameAndQuantity: Returns a list of item names and their corresponding quantities from the database.
     */
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
