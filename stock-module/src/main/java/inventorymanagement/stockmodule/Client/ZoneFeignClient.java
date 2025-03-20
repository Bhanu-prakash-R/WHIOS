package inventorymanagement.stockmodule.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Feign client interface for communicating with the "zone-module" service.
 *
 * This interface facilitates REST calls to interact with the "zone-module" service,
 * specifically for retrieving names of active zones.
 */
@FeignClient(name="zone-module")
public interface ZoneFeignClient {
	/**
     * Fetches the list of names of active zones from the "zone-module" service.
     *
     * This method sends a GET request to the endpoint "/api/zones/active/names" 
     * of the "zone-module" service and retrieves a list of names of currently active zones.
     *
     * @return A list of names of active zones as strings.
     */
	@GetMapping("/api/zones/active/names")
	public List<String> getNamesOfActiveZones();

}
