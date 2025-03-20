package com.example.performancemetrics.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.performancemetrics.Dto.PerformanceItemDto;
import com.example.performancemetrics.Dto.PerformancePurchaseDto;
import com.example.performancemetrics.Dto.PerformanceSalesDto;
import com.example.performancemetrics.Service.MetricsService;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private MetricsService metricsService;

    // Endpoint to fetch recent sales data
    @GetMapping("/recent-sales")
    public ResponseEntity<List<PerformanceSalesDto>> getRecentSales() {
        logger.info("Received request to fetch recent sales");

        List<PerformanceSalesDto> recentSales;

        try {
            recentSales = metricsService.getRecentSalesForDashboard();
            logger.info("Successfully fetched recent sales data for the dashboard");
        } catch (Exception e) {
            logger.error("Error while processing recent sales request: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }

        logger.info("Sending recent sales response to client");
        return ResponseEntity.ok(recentSales);
    }

    // Endpoint to fetch total revenue data
    @GetMapping("/total-revenues")
    public ResponseEntity<List<Object[]>> getTotalRevenues() {
        logger.info("Received request to fetch total revenue per item");

        List<Object[]> totalRevenues;

        try {
            totalRevenues = metricsService.getTotalRevenueForDashboard();
            logger.info("Successfully fetched total revenue data for the dashboard");
        } catch (Exception e) {
            logger.error("Error while processing total revenue request: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }

        logger.info("Sending total revenue response to client");
        return ResponseEntity.ok(totalRevenues);
    }
    @GetMapping("/stock")
    public ResponseEntity<List<PerformanceItemDto>> getStockMetrics() {
        List<PerformanceItemDto> stockMetrics = metricsService.getStockMetrics();
        return ResponseEntity.ok(stockMetrics);
    }
    
    @GetMapping("/recent-purchases")
    public ResponseEntity<List<PerformancePurchaseDto>> getRecentPurchases() {
        // Fetch recent purchases via service
        List<PerformancePurchaseDto> recentPurchases = metricsService.getRecentPurchases();
        return ResponseEntity.ok(recentPurchases);
    }

}
