package inventorymanagement.purchasemodule.Client;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import inventorymanagement.purchasemodule.dto.VendorResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;
/**
 * VendorFeignClient acts as a declarative REST client for interacting with the vendor module.
 * It uses Spring Cloud OpenFeign to enable seamless communication between microservices.
 * 
 * This interface defines the contract for fetching vendor names from the "vendor-module".
 */
@FeignClient(name="vendor-module")
public interface VendorFeignClient {
	
	/**
     * Retrieves a list of vendor names from the vendor module.
     * This method corresponds to the "/api/vendors/names" endpoint in the vendor module.
     *
     * @return a list of vendor names as {@code List<String>}
     */
	@GetMapping("/api/vendors/names")
	public List<String> getVendorNames();
	
	
	@GetMapping("/api/vendors")
    List<VendorResponseDto> getAllVendors();

    /**
     * Retrieves a vendor by its ID from the vendor module.
     *
     * @param id the vendor's unique ID
     * @return the vendor details as {@code VendorResponseDto}
     */
    @GetMapping("/api/vendors/{id}")
    VendorResponseDto getVendorById(@PathVariable UUID id);
}