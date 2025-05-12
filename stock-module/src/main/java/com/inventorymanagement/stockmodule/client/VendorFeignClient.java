package com.inventorymanagement.stockmodule.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Feign client interface to interact with the "vendor-module" service.
 *
 * This interface defines methods to invoke REST endpoints of the "vendor-module" service.
 */
@FeignClient(name="vendor-module")
public interface VendorFeignClient {
	/**
     * Retrieves a list of vendor names from the "vendor-module" service.
     *
     * This method sends a GET request to the endpoint "/api/vendors/names" 
     * of the "vendor-module" service and expects a list of vendor names as the response.
     *
     * @return List of vendor names as strings.
     */
	@GetMapping("/api/vendors/names")
	public List<String> getVendorNames();

}
