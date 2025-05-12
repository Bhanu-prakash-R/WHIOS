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

        List<PerformanceSalesDto> recentSales = metricsService.getRecentSalesForDashboard();
        logger.info("Successfully fetched recent sales data for the dashboard");
        
        logger.info("Sending recent sales response to client");
        return ResponseEntity.ok(recentSales);
    }

    // Endpoint to fetch total revenue data
    @GetMapping("/total-revenues")
    public ResponseEntity<List<Object[]>> getTotalRevenues() {
        logger.info("Received request to fetch total revenue per item");

        List<Object[]> totalRevenues = metricsService.getTotalRevenueForDashboard();
        logger.info("Successfully fetched total revenue data for the dashboard");

        logger.info("Sending total revenue response to client");
        return ResponseEntity.ok(totalRevenues);
    }

    @GetMapping("/stock")
    public ResponseEntity<List<PerformanceItemDto>> getStockMetrics() {
        logger.info("Received request to fetch stock metrics");

        List<PerformanceItemDto> stockMetrics = metricsService.getStockMetrics();
        logger.info("Successfully fetched stock metrics");

        logger.info("Sending stock metrics response to client");
        return ResponseEntity.ok(stockMetrics);
    }

    @GetMapping("/recent-purchases")
    public ResponseEntity<List<PerformancePurchaseDto>> getRecentPurchases() {
        logger.info("Received request to fetch recent purchases");

        List<PerformancePurchaseDto> recentPurchases = metricsService.getRecentPurchases();
        logger.info("Successfully fetched recent purchases");

        logger.info("Sending recent purchases response to client");
        return ResponseEntity.ok(recentPurchases);
    }
}
