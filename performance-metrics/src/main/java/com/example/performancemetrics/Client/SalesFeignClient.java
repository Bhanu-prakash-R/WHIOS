package com.example.performancemetrics.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.performancemetrics.Dto.PerformanceSalesDto;

@FeignClient(name = "sales-module")
public interface SalesFeignClient {

    // Fetch recent sales 
    @GetMapping("/api/sales/recent")
    List<PerformanceSalesDto> getRecentSales();

    // Fetch total revenue per item
    @GetMapping("/api/sales/revenues")
    List<Object[]> getTotalRevenuePerItem();
}
