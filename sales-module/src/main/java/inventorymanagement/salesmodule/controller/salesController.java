package inventorymanagement.salesmodule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inventorymanagement.salesmodule.Service.saleService;
import inventorymanagement.salesmodule.Dto.SaleRequestDto;
import inventorymanagement.salesmodule.Dto.SaleResponseDto;
import inventorymanagement.salesmodule.exception.SaleNotFoundException;
import jakarta.validation.Valid;
import inventorymanagement.salesmodule.exception.CustomerNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class salesController {

    private static final Logger logger = LoggerFactory.getLogger(salesController.class);

    @Autowired
    private saleService salesService;

    // Get all sales
    @GetMapping
    public ResponseEntity<List<SaleResponseDto>> getAllSales() {
        try {
            logger.info("Fetching all sales");
            List<SaleResponseDto> sales = salesService.getAllSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            logger.error("Error fetching all sales", e);
            throw new RuntimeException("Unable to fetch sales", e);
        }
    }

    // Get a single sale by ID
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDto> getSaleById(@PathVariable Long id) {
        try {
            logger.info("Fetching sale with ID: {}", id);
            SaleResponseDto sale = salesService.getSaleById(id);
            return ResponseEntity.ok(sale);
        } catch (SaleNotFoundException e) {
            logger.error("Sale not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching sale with ID: {}", id, e);
            throw new RuntimeException("Unable to fetch sale by ID", e);
        }
    }

    // Create a new sale
    @PostMapping
    public ResponseEntity<SaleResponseDto> createSale( @Valid @RequestBody SaleRequestDto saleRequestDto) {
        try {
            logger.info("Creating a new sale");
            SaleResponseDto newSale = salesService.createSale(saleRequestDto);
            return ResponseEntity.status(201).body(newSale);
        } catch (CustomerNotFoundException e) {
            logger.error("Customer not found while creating sale", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error creating sale", e);
            throw new RuntimeException("Unable to create sale", e);
        }
    }

    // Update a sale by ID
    @PutMapping("/{id}")
    public ResponseEntity<SaleResponseDto> updateSale(@PathVariable Long id, @RequestBody SaleRequestDto saleRequestDto) {
        try {
            logger.info("Updating sale with ID: {}", id);
            SaleResponseDto updatedSale = salesService.updateSale(id, saleRequestDto);
            return ResponseEntity.ok(updatedSale);
        } catch (SaleNotFoundException | CustomerNotFoundException e) {
            logger.error("Error updating sale with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating sale with ID: {}", id, e);
            throw new RuntimeException("Unable to update sale", e);
        }
    }

    // Get all sales associated with a specific customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleResponseDto>> getSalesByCustomer(@PathVariable Long customerId) {
        try {
            logger.info("Fetching sales for customer with ID: {}", customerId);
            List<SaleResponseDto> sales = salesService.getSalesByCustomer(customerId);
            return ResponseEntity.ok(sales);
        } catch (CustomerNotFoundException e) {
            logger.error("Customer not found with ID: {}", customerId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching sales for customer with ID: {}", customerId, e);
            throw new RuntimeException("Unable to fetch sales for customer", e);
        }
    }
    
    @GetMapping("/stock-item-names")
    public ResponseEntity<List<String>> getStockItemNames() {
        List<String> itemNames = salesService.getStockItemNames();
        return ResponseEntity.ok(itemNames);
    }

    // Delete a sale by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            logger.info("Deleting sale with ID: {}", id);
            salesService.deleteSale(id);
            return ResponseEntity.noContent().build();
        } catch (SaleNotFoundException e) {
            logger.error("Sale not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting sale with ID: {}", id, e);
            throw new RuntimeException("Unable to delete sale", e);
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Object[]>> getRecentSales() {
        logger.info("Fetching 3 recent sales");
        List<Object[]> recentSales = salesService.getRecentSales();
        return ResponseEntity.ok(recentSales);
    }
    
    @GetMapping("/revenues")
    public ResponseEntity<List<Object[]>> getTotalRevenuePerItem() {
        logger.info("Fetching total revenue per item");
        List<Object[]> totalRevenue = salesService.getTotalRevenuePerItem();
        return ResponseEntity.ok(totalRevenue);
    }


    
    
    
}
