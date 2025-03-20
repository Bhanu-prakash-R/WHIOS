package inventorymanagement.stockmodule.Client;

import java.util.List;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign client interface to interact with the "purchase-module" service.
 *
 * This interface defines methods to invoke REST endpoints of the "purchase-module" service.
 */
@FeignClient(name="purchase-module")
public interface PurchaseFeignClient {
	/**
     * Retrieves a list of item names from the "purchase-module" service.
     *
     * This method sends a GET request to the endpoint "/api/purchases/itemNames" 
     * of the "purchase-module" service and expects a list of item names as the response.
     *
     * @return List of item names as strings.
     */
	@GetMapping("/api/purchases/itemNames")
	List<String> getItemNames();
	

}
