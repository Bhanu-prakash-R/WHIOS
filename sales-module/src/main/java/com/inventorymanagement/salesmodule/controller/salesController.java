package com.inventorymanagement.salesmodule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventorymanagement.salesmodule.dto.SaleRequestDto;
import com.inventorymanagement.salesmodule.dto.SaleResponseDto;
import com.inventorymanagement.salesmodule.dto.SalesMetricsDto;
import com.inventorymanagement.salesmodule.response.ApiResponse;
import com.inventorymanagement.salesmodule.service.SaleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for handling sales-related API endpoints.
 */
@RestController
@RequestMapping("/api/sales")
public class salesController {

    private static final Logger logger = LoggerFactory.getLogger(salesController.class);

    @Autowired
    private SaleService salesService;

    /**
     * Retrieves all sales records.
     *
     * @return ResponseEntity containing the ApiResponse with the list of sales records.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getAllSales() {
        logger.info("Fetching all sales");
        List<SaleResponseDto> sales = salesService.getAllSales();
        ApiResponse<List<SaleResponseDto>> response = new ApiResponse<>(
            true,
            "Sales records retrieved successfully.",
            sales,
            200,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a sale record by its ID.
     *
     * @param id The ID (UUID) of the sale to fetch.
     * @return ResponseEntity containing the ApiResponse with sale details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleById(@PathVariable UUID id) { // Changed Long to UUID
        logger.info("Fetching sale with ID: {}", id);
        SaleResponseDto sale = salesService.getSaleById(id);
        ApiResponse<SaleResponseDto> response = new ApiResponse<>(
            true,
            "Sale record retrieved successfully.",
            sale,
            200,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new sale record.
     *
     * @param saleRequestDto The request body containing sale details.
     * @return ResponseEntity containing the ApiResponse with the created sale record.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponseDto>> createSale(@Valid @RequestBody SaleRequestDto saleRequestDto) {
        logger.info("Creating a new sale");
        SaleResponseDto newSale = salesService.createSale(saleRequestDto);
        ApiResponse<SaleResponseDto> response = new ApiResponse<>(
            true,
            "Sale record created successfully.",
            newSale,
            201,
            LocalDateTime.now()
        );
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Updates a sale record by its ID.
     *
     * @param id             The ID (UUID) of the sale to update.
     * @param saleRequestDto The request body containing updated sale details.
     * @return ResponseEntity containing the ApiResponse with the updated sale record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> updateSale(
            @PathVariable UUID id, // Changed Long to UUID
            @Valid @RequestBody SaleRequestDto saleRequestDto) {
        logger.info("Updating sale with ID: {}", id);
        SaleResponseDto updatedSale = salesService.updateSale(id, saleRequestDto);
        ApiResponse<SaleResponseDto> response = new ApiResponse<>(
            true,
            "Sale record updated successfully.",
            updatedSale,
            200,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves sales records for a specific customer by their ID.
     *
     * @param customerId The ID (UUID) of the customer.
     * @return ResponseEntity containing the ApiResponse with the list of sales for the customer.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getSalesByCustomer(@PathVariable UUID customerId) { // Changed Long to UUID
        logger.info("Fetching sales for customer with ID: {}", customerId);
        List<SaleResponseDto> sales = salesService.getSalesByCustomer(customerId);
        ApiResponse<List<SaleResponseDto>> response = new ApiResponse<>(
            true,
            "Sales records for customer retrieved successfully.",
            sales,
            200,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of stock item names.
     *
     * @return ResponseEntity containing the ApiResponse with the list of stock item names.
     */
    @GetMapping("/stock-item-names")
    public ResponseEntity<ApiResponse<List<String>>> getStockItemNames() {
        logger.info("Fetching stock item names");
        List<String> itemNames = salesService.getStockItemNames();
        ApiResponse<List<String>> response = new ApiResponse<>(
            true,
            "Stock item names retrieved successfully.",
            itemNames,
            200,
            LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a sale record by its ID.
     *
     * @param id The ID (UUID) of the sale to delete.
     * @return ResponseEntity containing the ApiResponse with no data.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSale(@PathVariable UUID id) { // Changed Long to UUID
        logger.info("Deleting sale with ID: {}", id);
        salesService.deleteSale(id);
        ApiResponse<Void> response = new ApiResponse<>(
            true,
            "Sale record deleted successfully.",
            null,
            204,
            LocalDateTime.now()
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the three most recent sales records.
     *
     * @return ResponseEntity containing the ApiResponse with the recent sales records.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<SalesMetricsDto>> getRecentSales() {
        logger.info("Fetching 3 recent sales");
        List<SalesMetricsDto> recentSales = salesService.getRecentSales();
        return ResponseEntity.ok(recentSales);
    }

    /**
     * Retrieves the total revenue generated for each stock item.
     *
     * @return ResponseEntity containing the ApiResponse with the total revenues per item.
     */
    @GetMapping("/revenues")
    public ResponseEntity<List<Object[]>> getTotalRevenuePerItem() {
        logger.info("Fetching total revenue per item");
        List<Object[]> totalRevenue = salesService.getTotalRevenuePerItem();
        return ResponseEntity.ok(totalRevenue);
    }
}
