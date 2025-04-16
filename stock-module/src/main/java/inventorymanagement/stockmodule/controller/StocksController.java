package inventorymanagement.stockmodule.controller;

import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inventorymanagement.stockmodule.dto.ItemNameQuantityDto;
import inventorymanagement.stockmodule.dto.StockDTO;
import inventorymanagement.stockmodule.entity.Notification;
import inventorymanagement.stockmodule.exception.InsufficientStockException;
import inventorymanagement.stockmodule.exception.StockNotFoundException;
import inventorymanagement.stockmodule.service.StocksService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for handling stock-related API endpoints.
 *
 * This controller provides APIs under the "/api/stocks" path and uses 
 * SLF4J logging for managing logs.
 */
@Slf4j
@RestController
@RequestMapping("/api/stocks")

/**
 * Controller class for managing stock-related operations.
 *
 * This class acts as the entry point for handling API requests related to stocks.
 * It utilizes the `StocksService` to execute business logic and interact with the data layer.
 */
public class StocksController {

    @Autowired
    private StocksService stocksService;

    // 1. Create Stock
    /**
     * Creates a new stock entry.
     *
     * @param stockDTO The stock details to be created.
     * @return Response with the created stock or error status.
     */
    @PostMapping("/create")
    public ResponseEntity<StockDTO> createStock(@Valid @RequestBody StockDTO stockDTO) {
        try {
            log.info("Creating stock with itemName: {}", stockDTO.getItemName());
            StockDTO createdStock = stocksService.createStock(stockDTO);
            log.info("Stock created successfully: {}", createdStock);
            return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating stock: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{stockId}")
    public ResponseEntity<StockDTO> getStockById(@PathVariable UUID stockId) {
        log.info("Received request to get stock by ID: {}", stockId);
        try {
            StockDTO stockDTO = stocksService.getStockById(stockId);
            log.info("Successfully fetched stock with ID: {}", stockId);
            return new ResponseEntity<>(stockDTO, HttpStatus.OK);
        } catch (StockNotFoundException e) {
            log.error("Stock not found for ID: {}", stockId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching stock with ID: {}", stockId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Display All Stocks
    /**
     * Retrieves all stock entries.
     *
     * @return Response with the list of stocks or an error status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        try {
            log.info("Fetching all stocks");
            List<StockDTO> stocks = stocksService.getAllStocks();
            log.info("Fetched all stocks successfully");
            return new ResponseEntity<>(stocks, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching stocks: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Update Stock
    /**
    * Updates a stock.
    *
    * @param stockId ID of the stock.
    * @param stockDTO Updated stock details.
    * @return HTTP status: 200 (OK), 404 (NOT_FOUND), or 500 (ERROR).
    */
    @PutMapping("/update/{stockId}")
    public ResponseEntity<Void> updateStock(@PathVariable String stockId,@Valid @RequestBody StockDTO stockDTO) {
        try {
            log.info("Updating stock with ID: {}", stockId);
            stocksService.updateStock(stockId, stockDTO);
            log.info("Updated stock with ID: {}", stockId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StockNotFoundException e) {
            log.error("Stock not found: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Retrieves the list of vendor names.
     *
     * @return Response containing the list of vendor names with HTTP status 200.
     */
    @GetMapping("/vendorNames")
    public ResponseEntity<List<String>> getVendorNames(){
    	List<String> vendorNames=stocksService.getVendorNames();
    	return  ResponseEntity.ok(vendorNames);
    }
    
    /**
     * Retrieves the list of active zone names.
     *
     * @return Response containing the list of active zone names with HTTP status 200.
     */
    @GetMapping("/ActiveZoneNames")
    public ResponseEntity<List<String>> getActiveZoneNames(){
    	List<String> zoneNames=stocksService.getNamesOfActiveZones();
    	return ResponseEntity.ok(zoneNames);
    }
    
   


    // 4. Remove Stock
    /**
     * Removes a specified quantity of a stock item.
     *
     * @param stockId ID of the stock item.
     * @param quantity Quantity to remove.
     * @return HTTP status: 200 (OK), 404 (NOT_FOUND), 400 (BAD_REQUEST), or 500 (ERROR).
     */
//    @DeleteMapping("/remove/{stockId}/{quantity}")
//    public ResponseEntity<Void> removeItem(@PathVariable UUID stockId, @PathVariable int quantity) {
//        try {
//            log.info("Removing item with stock ID: {} by quantity: {}", stockId, quantity);
//            stocksService.removeItem(stockId, quantity);
//            log.info("Removed item with stock ID: {}", stockId);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (StockNotFoundException e) {
//            log.error("Stock not found: {}", e.getMessage(), e);
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (InsufficientStockException e) {
//            log.error("Insufficient stock: {}", e.getMessage(), e);
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Error removing item: {}", e.getMessage(), e);
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    
    
    

    // 5. Restock Item
    /**
     * Restocks a specified quantity of a stock item.
     *
     * @param stockId ID of the stock item.
     * @param quantity Quantity to restock.
     * @return HTTP status: 200 (OK), 404 (NOT_FOUND), or 500 (ERROR).
     */
    @PutMapping("/restock/{stockId}/{quantity}")
    public ResponseEntity<Void> restockItem(@PathVariable UUID stockId, @PathVariable int quantity) {
        try {
            log.info("Restocking item with stock ID: {} by quantity: {}", stockId, quantity);
            stocksService.restockItem(stockId, quantity);
            log.info("Restocked item with stock ID: {}", stockId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StockNotFoundException e) {
            log.error("Stock not found: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error restocking item: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> deleteStock(@PathVariable UUID stockId) {
        try {
            stocksService.deleteStockById(stockId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (StockNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // 6. Check and Notify Low Stock
   /* @GetMapping("/checkAndNotify/{stockId}")
    public ResponseEntity<StockDTO> checkAndNotifyStock(@PathVariable UUID stockId) {
        try {
            log.info("Checking and notifying stock with ID: {}", stockId);
            StockDTO stock = stocksService.getStockById(stockId);
            StockDTO updatedStock = stocksService.checkAndNotifyLowStock(stock);
            log.info("Checked and notified stock with ID: {}", stockId);
            return new ResponseEntity<>(updatedStock, HttpStatus.OK);
        } catch (StockNotFoundException e) {
            log.error("Stock not found: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error checking and notifying stock: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    // 7. Scheduler for Low Stock Notification
    /**
     * Triggers a manual low stock notification.
     *
     * @return HTTP status: 200 (OK) on success or 500 (ERROR) on failure.
     */
    @GetMapping("/checkLowStock")
    public ResponseEntity<List<String>> checkAndNotifyLowStock() {
        try {
            log.info("Manually triggering low stock notification");
            List<Notification> lowStockNotifications = stocksService.scheduledLowStockCheck();
            log.info("Triggered low stock notification");
 
            // Extract messages from notifications
            List<String> notificationMessages = lowStockNotifications.stream()
                .map(Notification::getMessage)
                .collect(Collectors.toList());
 
            return new ResponseEntity<>(notificationMessages, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error triggering low stock notification: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    
    /**
     * Checks the availability of a stock item.
     *
     * @param itemName Name of the item to check.
     * @param quantity Quantity required.
     * @return Response with true if available, false otherwise.
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkStockAvailability(@RequestParam String itemName, @RequestParam int quantity) {
        boolean isAvailable = stocksService.checkStockAvailability(itemName, quantity);
        return ResponseEntity.ok(isAvailable);
    }
    
    @GetMapping("/details")
    public ResponseEntity<StockDTO> getStockDetails(@RequestParam String itemName) {
        log.info("Request received to fetch stock details for item: {}", itemName);

        try {
            StockDTO stockDTO = stocksService.getStockDetails(itemName);
            return ResponseEntity.ok(stockDTO);
        } catch (IllegalArgumentException e) {
            log.error("Error fetching stock details: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates the quantity of a stock item.
     *
     * @param itemName Name of the stock item.
     * @param quantity New quantity to update.
     * @return HTTP status: 204 (NO_CONTENT) on successful update.
     */
    @PutMapping("/updateDetails")
    public ResponseEntity<String> updateStockDetails(
            @RequestParam String itemName, 
            @RequestParam int quantity, 
            @RequestParam double price) {
        try {
            stocksService.updateStockDetails(itemName, quantity, price);
            return ResponseEntity.ok("Stock details updated successfully for item: " + itemName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating stock details for item '{}': {}", itemName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update stock details");
        }
    }


    // 8. Fetch Only Stock Names
    /**
     * Retrieves the names of all stock items.
     *
     * @return Response containing a list of stock names with HTTP status 200,
     *         or HTTP status 500 in case of an error.
     */
    @GetMapping("/StockItemNames")
    public ResponseEntity<List<String>> getAllStockNames() {
        try {
            log.info("Fetching all stock names");
            List<String> stockNames = stocksService.getAllStockNames();
            log.info("Fetched stock names successfully");
            return new ResponseEntity<>(stockNames, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching stock names: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Retrieves all item names from the purchase module.
     *
     * @return Response containing a list of purchase item names with HTTP status 200.
     */
    @GetMapping("/puchaseItemNames")
    public ResponseEntity<List<String>> getItemNames(){
    	log.info("fetching all itemNames from purchase");
    	List<String> purchaseItemNames=stocksService.getItemNames();
    	return ResponseEntity.ok(purchaseItemNames);
    }
    
    /**
     * Retrieves the list of available item names for stock.
     *
     * @return Response containing a list of available item names with HTTP status 200.
     */
    @GetMapping("/available-item-names")
    public ResponseEntity<List<String>> getAvailableItemNamesForStock() {
        List<String> availableItemNames = stocksService.getAvailableItemNamesForStock();
        return ResponseEntity.ok(availableItemNames);
    }
    
    /**
     * Retrieves stock metrics including item names and their quantities.
     *
     * @return Response containing a list of item name and quantity details with HTTP status 200.
     */
    @GetMapping("/metrics")
    public ResponseEntity<List<ItemNameQuantityDto>> getItemNameAndQuantity() {
        log.info("Received request to fetch stock metrics");

        List<ItemNameQuantityDto> stockMetrics = stocksService.getItemNameAndQuantity();
        log.info("Returning stock metrics with {} records", stockMetrics.size());

        return ResponseEntity.ok(stockMetrics);
    }


    
    /*@GetMapping("/stockMetrics")
    public ResponseEntity<List<Map<String, Object>>> getGraphMetrics() {
        log.info("Received request to fetch graph metrics for stock");

        try {
            List<Map<String, Object>> graphMetrics = stocksService.getGraphMetrics();
            log.info("Successfully fetched graph metrics, data size: {}", graphMetrics.size());
            return ResponseEntity.ok(graphMetrics);

        } catch (Exception ex) {
            log.error("An error occurred while fetching graph metrics: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }*/
}
