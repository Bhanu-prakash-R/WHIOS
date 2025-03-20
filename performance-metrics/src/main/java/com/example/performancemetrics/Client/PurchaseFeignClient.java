package com.example.performancemetrics.Client;

import java.util.List;
import com.example.performancemetrics.Dto.PerformancePurchaseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="purchase-module")
public interface PurchaseFeignClient {
	
	
	@GetMapping("/api/purchases/recent")
    List<PerformancePurchaseDto> getRecentPurchases(@RequestParam(defaultValue = "0") int page, 
                                                    @RequestParam(defaultValue = "4") int size);

}
