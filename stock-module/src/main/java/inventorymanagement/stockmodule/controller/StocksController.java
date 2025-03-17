package inventorymanagement.stockmodule.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import inventorymanagement.stockmodule.exception.InsufficientStockException;
import inventorymanagement.stockmodule.exception.StockNotFoundException;
import inventorymanagement.stockmodule.service.StocksService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stocks")
public class StocksController {

    @Autowired
    private StocksService stocksService;

    // 1. Create Stock
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

    // 2. Display All Stocks
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
    
    @GetMapping("/vendorNames")
    public ResponseEntity<List<String>> getVendorNames(){
    	List<String> vendorNames=stocksService.getVendorNames();
    	return  ResponseEntity.ok(vendorNames);
    }
    
    @GetMapping("/ActiveZoneNames")
    public ResponseEntity<List<String>> getActiveZoneNames(){
    	List<String> zoneNames=stocksService.getNamesOfActiveZones();
    	return ResponseEntity.ok(zoneNames);
    }
    
   


    // 4. Remove Stock
    @DeleteMapping("/remove/{stockId}/{quantity}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID stockId, @PathVariable int quantity) {
        try {
            log.info("Removing item with stock ID: {} by quantity: {}", stockId, quantity);
            stocksService.removeItem(stockId, quantity);
            log.info("Removed item with stock ID: {}", stockId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StockNotFoundException e) {
            log.error("Stock not found: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InsufficientStockException e) {
            log.error("Insufficient stock: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error removing item: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Restock Item
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
    @GetMapping("/checkLowStock")
    public ResponseEntity<Void> checkAndNotifyLowStock() {
        try {
            log.info("Manually triggering low stock notification");
            stocksService.checkAndNotifyLowStock();
            log.info("Triggered low stock notification");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error triggering low stock notification: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkStockAvailability(@RequestParam String itemName, @RequestParam int quantity) {
        boolean isAvailable = stocksService.checkStockAvailability(itemName, quantity);
        return ResponseEntity.ok(isAvailable);
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<Void> updateStockQuantity(@RequestParam String itemName, @RequestParam int quantity) {
        stocksService.updateStockQuantity(itemName, quantity);
        return ResponseEntity.noContent().build();
    }

    // 8. Fetch Only Stock Names
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
    @GetMapping("/puchaseItemNames")
    public ResponseEntity<List<String>> getItemNames(){
    	log.info("fetching all itemNames from purchase");
    	List<String> purchaseItemNames=stocksService.getItemNames();
    	return ResponseEntity.ok(purchaseItemNames);
    }
    
    @GetMapping("/available-item-names")
    public ResponseEntity<List<String>> getAvailableItemNamesForStock() {
        List<String> availableItemNames = stocksService.getAvailableItemNamesForStock();
        return ResponseEntity.ok(availableItemNames);
    }
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
