package inventorymanagement.salesmodule.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="stock-module")
public interface StockFeignClient {
	@GetMapping("/api/stocks/StockItemNames")
    ResponseEntity<List<String>> getStockItemNames();
	@GetMapping("/api/stocks/check-availability")
    ResponseEntity<Boolean> checkStockAvailability(@RequestParam String itemName, @RequestParam int quantity);

    @PutMapping("/api/stocks/update-quantity")
    ResponseEntity<Void> updateStockQuantity(@RequestParam String itemName, @RequestParam int quantity);

}
