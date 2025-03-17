package com.example.performancemetrics.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.performancemetrics.Client.SalesFeignClient;
import com.example.performancemetrics.Client.StockFeignClient;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);

    @Autowired
    private SalesFeignClient salesFeignClient;
    
    @Autowired
    private StockFeignClient stockFeignClient;

    public List<Map<String, Object>> getStockMetrics() {
        List<Object[]> results = stockFeignClient.getStockMetrics();
        
        // Transform raw Object[] data into a list of maps
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("itemName", result[0]); // Cast itemName to String
                    map.put("quantity", result[1]); // Cast quantity to an appropriate type
                    return map;
                })
                .collect(Collectors.toList());
    }

    // Fetch recent sales for the dashboard
    public List<Object[]> getRecentSalesForDashboard() {
        logger.info("Entering getRecentSalesForDashboard method");

        List<Object[]> recentSales = null;

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
