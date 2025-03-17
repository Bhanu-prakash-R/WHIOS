package inventorymanagement.stockmodule.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="zone-module")
public interface ZoneFeignClient {
	@GetMapping("/api/zones/active/names")
	public List<String> getNamesOfActiveZones();

}
