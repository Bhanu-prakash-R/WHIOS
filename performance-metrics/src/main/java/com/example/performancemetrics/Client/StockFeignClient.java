package com.example.performancemetrics.Client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
//import inventorymanagement.stockmodule.dto.ItemNameQuantityDto;

@FeignClient(name = "stock-module")
public interface StockFeignClient {

    @GetMapping("/api/stocks/metrics")
    List<Map<String,Object>> getStockMetrics();
}
