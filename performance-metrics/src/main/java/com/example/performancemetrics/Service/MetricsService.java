package com.example.performancemetrics.Service;

import java.util.List;

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
import com.example.performancemetrics.exception.ServiceException;

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
        try {
            return purchaseFeignClient.getRecentPurchases(0, 4); // Fetch top 4 recent purchases
        } catch (Exception e) {
            logger.error("Error fetching recent purchases: {}", e.getMessage());
            throw new ServiceException("Unable to fetch recent purchases.");
        }
    }

    public List<PerformanceItemDto> getStockMetrics() {
        try {
            logger.info("Calling StockFeignClient to fetch metrics...");
            return stockFeignClient.getStockMetrics();
        } catch (Exception e) {
            logger.error("Error fetching stock metrics: {}", e.getMessage());
            throw new ServiceException("Unable to fetch stock metrics.");
        }
    }

    public List<PerformanceSalesDto> getRecentSalesForDashboard() {
        logger.info("Entering getRecentSalesForDashboard method");
        try {
            List<PerformanceSalesDto> recentSales = salesFeignClient.getRecentSales();
            logger.info("Successfully fetched {} recent sales from Sales module", recentSales.size());
            return recentSales;
        } catch (Exception e) {
            logger.error("Error fetching recent sales from Sales module: {}", e.getMessage());
            throw new ServiceException("Unable to fetch recent sales.");
        }
    }

    public List<Object[]> getTotalRevenueForDashboard() {
        logger.info("Entering getTotalRevenueForDashboard method");
        try {
            List<Object[]> totalRevenues = salesFeignClient.getTotalRevenuePerItem();
            logger.info("Successfully fetched total revenues for {} items from Sales module", totalRevenues.size());
            return totalRevenues;
        } catch (Exception e) {
            logger.error("Error fetching total revenues from Sales module: {}", e.getMessage());
            throw new ServiceException("Unable to fetch total revenue data.");
        }
    }
}
