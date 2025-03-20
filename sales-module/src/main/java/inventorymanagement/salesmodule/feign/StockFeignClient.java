package inventorymanagement.salesmodule.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * Declares a Feign client for communication with the "stock-module" service.
 * - The @FeignClient annotation specifies the name of the service being called.
 * - Enables inter-service communication in a microservices architecture.
 */

@FeignClient(name="stock-module")
/**
 * Defines REST API endpoints for stock-related operations:
 * 
 * - getStockItemNames: Retrieves the list of stock item names.
 * - checkStockAvailability: Checks the availability of a specific stock item and its required quantity.
 * - updateStockQuantity: Updates the quantity of a specific stock item.
 *
 * Each method communicates with the stock module and uses ResponseEntity to wrap the responses.
 */
public interface StockFeignClient {
	@GetMapping("/api/stocks/StockItemNames")
    ResponseEntity<List<String>> getStockItemNames();
	@GetMapping("/api/stocks/check-availability")
    ResponseEntity<Boolean> checkStockAvailability(@RequestParam String itemName, @RequestParam int quantity);

    @PutMapping("/api/stocks/update-quantity")
    ResponseEntity<Void> updateStockQuantity(@RequestParam String itemName, @RequestParam int quantity);

}
