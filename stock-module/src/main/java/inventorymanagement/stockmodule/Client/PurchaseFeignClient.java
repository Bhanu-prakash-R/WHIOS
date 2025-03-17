package inventorymanagement.stockmodule.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="purchase-module")
public interface PurchaseFeignClient {
	@GetMapping("/api/purchases/itemNames")
	List<String> getItemNames();
	

}
