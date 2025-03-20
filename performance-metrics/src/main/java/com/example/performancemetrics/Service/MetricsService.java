package com.example.performancemetrics.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.performancemetrics.Client.PurchaseFeignClient;
import com.example.performancemetrics.Client.SalesFeignClient;
import com.example.performancemetrics.Client.StockFeignClient;
import com.example.performancemetrics.Dto.PerformanceItemDto;
import com.example.performancemetrics.Dto.PerformancePurchaseDto;
import com.example.performancemetrics.Dto.PerformanceSalesDto;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);

    @Autowired
    private SalesFeignClient salesFeignClient;
    
    @Autowired
    private StockFeignClient stockFeignClient;
    
    @Autowired
    private PurchaseFeignClient purchaseFeignClient;

    public List<PerformancePurchaseDto> getRecentPurchases() {
        // Call the Purchase module's Feign client
        return purchaseFeignClient.getRecentPurchases(0, 4); // Fetch top 4 recent purchases
    }

    public List<PerformanceItemDto> getStockMetrics() {
        logger.info("Calling StockFeignClient to fetch metrics...");
        List<Map<String, Object>> results = stockFeignClient.getStockMetrics();

        logger.info("Received {} records from Stock module", results.size());

        // Transform the raw data into PerformanceItemDto
        return results.stream()
                .map(result -> new PerformanceItemDto(
                        (String) result.get("itemName"), // Ensure key matches Stock module JSON field
                        ((Number) result.get("quantity")).intValue() // Convert quantity to int
                ))
                .collect(Collectors.toList());
    }



    // Fetch recent sales for the dashboard
    public List<PerformanceSalesDto> getRecentSalesForDashboard() {
        logger.info("Entering getRecentSalesForDashboard method");

        List<PerformanceSalesDto> recentSales = null;

        try {
            recentSales = salesFeignClient.getRecentSales();
            logger.info("Successfully fetched {} recent sales from Sales module", 
                        (recentSales != null) ? recentSales.size() : 0);
        } catch (Exception e) {
            logger.error("Error fetching recent sales from Sales module: {}", e.getMessage());
            throw e; // Re-throw to handle further up
        }

        logger.info("Exiting getRecentSalesForDashboard method");
        return recentSales;
    }

    // Fetch total revenue per item for the dashboard
    public List<Object[]> getTotalRevenueForDashboard() {
        logger.info("Entering getTotalRevenueForDashboard method");

        List<Object[]> totalRevenues = null;

        try {
            totalRevenues = salesFeignClient.getTotalRevenuePerItem();
            logger.info("Successfully fetched total revenues for {} items from Sales module", 
                        (totalRevenues != null) ? totalRevenues.size() : 0);
        } catch (Exception e) {
            logger.error("Error fetching total revenues from Sales module: {}", e.getMessage());
            throw e; // Re-throw to handle further up
        }

        logger.info("Exiting getTotalRevenueForDashboard method");
        return totalRevenues;
    }
}
