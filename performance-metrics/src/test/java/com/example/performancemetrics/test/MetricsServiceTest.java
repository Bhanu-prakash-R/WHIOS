package com.example.performancemetrics.test;


import com.example.performancemetrics.Client.PurchaseFeignClient;
import com.example.performancemetrics.Client.SalesFeignClient;
import com.example.performancemetrics.Client.StockFeignClient;
import com.example.performancemetrics.Dto.PerformanceItemDto;
import com.example.performancemetrics.Dto.PerformancePurchaseDto;
import com.example.performancemetrics.Dto.PerformanceSalesDto;
import com.example.performancemetrics.Service.MetricsService;
import com.example.performancemetrics.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MetricsServiceTest {

    @Mock
    private SalesFeignClient salesFeignClient;

    @Mock
    private StockFeignClient stockFeignClient;

    @Mock
    private PurchaseFeignClient purchaseFeignClient;

    @Mock
    private Logger logger; // Mock the logger

    @InjectMocks
    private MetricsService metricsService;

    @Test
    void getRecentPurchases_success() {
        // Arrange
        List<PerformancePurchaseDto> mockPurchases = Arrays.asList(
                new PerformancePurchaseDto(),
                new PerformancePurchaseDto()
        );
        when(purchaseFeignClient.getRecentPurchases(0, 4)).thenReturn(mockPurchases);

        // Act
        List<PerformancePurchaseDto> recentPurchases = metricsService.getRecentPurchases();

        // Assert
        assertEquals(mockPurchases.size(), recentPurchases.size());
        verify(purchaseFeignClient, times(1)).getRecentPurchases(0, 4);
    }

    @Test
    void getRecentPurchases_failure() {
        // Arrange
        when(purchaseFeignClient.getRecentPurchases(0, 4)).thenThrow(new RuntimeException("Purchase service down"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> metricsService.getRecentPurchases());
        assertEquals("Unable to fetch recent purchases.", exception.getMessage());
        verify(purchaseFeignClient, times(1)).getRecentPurchases(0, 4);
        // Optionally verify logging of the error
        // verify(logger).error(anyString(), anyString());
    }

    @Test
    void getStockMetrics_success() {
        // Arrange
        List<PerformanceItemDto> mockStockMetrics = Arrays.asList(
                new PerformanceItemDto(),
                new PerformanceItemDto(),
                new PerformanceItemDto()
        );
        when(stockFeignClient.getStockMetrics()).thenReturn(mockStockMetrics);

        // Act
        List<PerformanceItemDto> stockMetrics = metricsService.getStockMetrics();

        // Assert
        assertEquals(mockStockMetrics.size(), stockMetrics.size());
        verify(stockFeignClient, times(1)).getStockMetrics();
    }

    @Test
    void getStockMetrics_failure() {
        // Arrange
        when(stockFeignClient.getStockMetrics()).thenThrow(new RuntimeException("Stock service unavailable"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> metricsService.getStockMetrics());
        assertEquals("Unable to fetch stock metrics.", exception.getMessage());
        verify(stockFeignClient, times(1)).getStockMetrics();
        // Optionally verify logging of the error
        // verify(logger).error(anyString(), anyString());
    }

    

    @Test
    void getRecentSalesForDashboard_failure() {
        // Arrange
        when(salesFeignClient.getRecentSales()).thenThrow(new RuntimeException("Sales data not available"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> metricsService.getRecentSalesForDashboard());
        assertEquals("Unable to fetch recent sales.", exception.getMessage());
        verify(salesFeignClient, times(1)).getRecentSales();
        // Optionally verify logging of the error
        // verify(logger).error(anyString(), anyString());
    }

    @Test
    void getTotalRevenueForDashboard_success() {
        // Arrange
        List<Object[]> mockTotalRevenues = Arrays.asList(
                new Object[]{"Item A", 100.0},
                new Object[]{"Item B", 150.0}
        );
        when(salesFeignClient.getTotalRevenuePerItem()).thenReturn(mockTotalRevenues);

        // Act
        List<Object[]> totalRevenues = metricsService.getTotalRevenueForDashboard();

        // Assert
        assertEquals(mockTotalRevenues.size(), totalRevenues.size());
        verify(salesFeignClient, times(1)).getTotalRevenuePerItem();
    }

    @Test
    void getTotalRevenueForDashboard_failure() {
        // Arrange
        when(salesFeignClient.getTotalRevenuePerItem()).thenThrow(new RuntimeException("Revenue data error"));

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> metricsService.getTotalRevenueForDashboard());
        assertEquals("Unable to fetch total revenue data.", exception.getMessage());
        verify(salesFeignClient, times(1)).getTotalRevenuePerItem();
        // Optionally verify logging of the error
        // verify(logger).error(anyString(), anyString());
    }
}