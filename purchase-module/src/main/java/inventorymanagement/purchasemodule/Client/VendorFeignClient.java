package inventorymanagement.purchasemodule.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="vendor-module")
public interface VendorFeignClient {
	@GetMapping("/api/vendors/names")
	public List<String> getVendorNames();
}