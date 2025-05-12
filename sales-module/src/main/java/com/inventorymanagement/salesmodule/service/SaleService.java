package com.inventorymanagement.salesmodule.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.inventorymanagement.salesmodule.dto.CustomerResponseDto;
import com.inventorymanagement.salesmodule.dto.SaleRequestDto;
import com.inventorymanagement.salesmodule.dto.SaleResponseDto;
import com.inventorymanagement.salesmodule.dto.SalesMetricsDto;
import com.inventorymanagement.salesmodule.dto.StockDTO;
import com.inventorymanagement.salesmodule.exception.CustomerNotFoundException;
import com.inventorymanagement.salesmodule.exception.InsufficientStockException;
import com.inventorymanagement.salesmodule.exception.SaleNotFoundException;
import com.inventorymanagement.salesmodule.feign.StockFeignClient;
import com.inventorymanagement.salesmodule.model.Customer;
import com.inventorymanagement.salesmodule.model.Sales;
import com.inventorymanagement.salesmodule.repository.salesRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    @Autowired
    private salesRepo repository;

    @Autowired
    private customerService customerService;
    
    @Autowired
    private StockFeignClient stockFeignClient;

    /**
     * Fetches a list of stock item names using the StockFeignClient.
     */
    public List<String> getStockItemNames() {
        return stockFeignClient.getStockItemNames().getBody();
    }

    public List<SaleResponseDto> getAllSales() {
        logger.info("Fetching all sales from the database...");

        // Retrieve all sales from the repository
        List<Sales> salesList = repository.findAll();

        // Map each Sale entity to a SaleResponseDto and calculate total price
        List<SaleResponseDto> saleResponseList = salesList.stream().map(sale -> {
            double totalPrice = sale.getPrice() * sale.getQuantity(); // Calculate total price

            // Map the entity to the response DTO, including the address
            return new SaleResponseDto(
                sale.getSaleId(),
                sale.getItemName(),
                sale.getQuantity(),
                totalPrice, // Total price for the 'price' attribute
                sale.getSaleDate(),
                new CustomerResponseDto(
                    sale.getCustomer().getCustomerId(),
                    sale.getCustomer().getName(),
                    sale.getCustomer().getContactDetails().getPhoneNumber(),
                    sale.getCustomer().getContactDetails().getEmail(),
                    sale.getCustomer().getContactDetails().getAddress() // Add address here
                )
            );
        }).collect(Collectors.toList());

        logger.info("Successfully processed {} sales records.", saleResponseList.size());
        return saleResponseList;
    }



    public SaleResponseDto getSaleById(UUID id) { 
        logger.info("Entering getSaleById method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));
        SaleResponseDto saleResponse = new SaleResponseDto(
            sale.getSaleId(),
            sale.getItemName(),
            sale.getQuantity(),
            sale.getPrice(),
            sale.getSaleDate(),
            new CustomerResponseDto(
                sale.getCustomer().getCustomerId(),
                sale.getCustomer().getName(),
                sale.getCustomer().getContactDetails().getPhoneNumber(),
                sale.getCustomer().getContactDetails().getEmail(),
                sale.getCustomer().getContactDetails().getAddress()
                
            )
        );
        logger.info("Exiting getSaleById method with sale: {}", saleResponse);
        return saleResponse;
    }

    @Transactional
    public SaleResponseDto createSale(SaleRequestDto saleRequestDto) {
        logger.info("Processing sale for item: {}", saleRequestDto.getItemName());
 
        // Fetch stock details
        ResponseEntity<StockDTO> stockDetailsResponse = stockFeignClient.getStockDetails(saleRequestDto.getItemName());
        StockDTO stockDetails = stockDetailsResponse.getBody();
 
        if (stockDetails == null || stockDetails.getPrice() <= 0 || stockDetails.getQuantity() <= 0) {
            throw new IllegalArgumentException("Stock details not available or invalid for item: " + saleRequestDto.getItemName());
        }
 
        logger.info("Fetched stock details: total price={}, quantity={}", stockDetails.getPrice(), stockDetails.getQuantity());
 
        // Calculate remaining quantity
        int remainingQuantity = stockDetails.getQuantity() - saleRequestDto.getQuantity();
        if (remainingQuantity < 0) {
            throw new InsufficientStockException("Insufficient stock for item: " 
                + saleRequestDto.getItemName() 
                + ". Available: " + stockDetails.getQuantity() 
                + ", Requested: " + saleRequestDto.getQuantity());
        }
 
        // Create sale entry using total sale price
        Customer customer = customerService.getOrCreateCustomer(
            saleRequestDto.getCustomerName(),
            saleRequestDto.getCustomerPhone(),
            saleRequestDto.getCustomerEmail(),
            saleRequestDto.getCustomerAddress()
        );
        
 
        Sales sale = new Sales();
        sale.setCustomer(customer);
        sale.setItemName(saleRequestDto.getItemName());
        sale.setQuantity(saleRequestDto.getQuantity());
        sale.setPrice(saleRequestDto.getPrice()); // Calculate total sale price
        sale.setSaleDate(LocalDateTime.now());
 
        Sales newSale = repository.save(sale);
        logger.info("Created new sale: {}", newSale);
 
        
        
        // Calculate per-item price based on the total price
        double perItemPrice = stockDetails.getPrice();  // stockDetails.getQuantity();
        logger.info("Per-item price calculated as: ₹{}", perItemPrice);
 
        // Calculate the updated total price for remaining stock
       // double updatedInventoryPrice = perItemPrice * remainingQuantity; // Total price calculation
        //logger.info("Updated inventory total price for remaining stock: ₹{}", updatedInventoryPrice);
 
        // Update inventory with the correct total price
        stockFeignClient.updateStockDetails(saleRequestDto.getItemName(), remainingQuantity, perItemPrice);
 
   
        
        
        double totalPrice = sale.getPrice();
 
        // Build and return response DTO
        SaleResponseDto response = new SaleResponseDto(
            newSale.getSaleId(),
            newSale.getItemName(),
            newSale.getQuantity(),
            totalPrice,
            newSale.getSaleDate(),
            new CustomerResponseDto(
                customer.getCustomerId(),
                customer.getName(),
                customer.getContactDetails().getPhoneNumber(),
                customer.getContactDetails().getEmail(),
                customer.getContactDetails().getAddress()
            )
        );
 
        logger.info("Sale processed successfully: {}", response);
        return response;
    }
 
 

    public SaleResponseDto updateSale(UUID id, SaleRequestDto saleRequestDto) { // Changed Long to UUID
        logger.info("Entering updateSale method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));

        Customer customer = customerService.getOrCreateCustomer(
            saleRequestDto.getCustomerName(),
            saleRequestDto.getCustomerPhone(),
            saleRequestDto.getCustomerEmail(),
            saleRequestDto.getCustomerAddress()
        );
        sale.setCustomer(customer);

        sale.setItemName(saleRequestDto.getItemName());
        sale.setQuantity(saleRequestDto.getQuantity());
        sale.setPrice(saleRequestDto.getPrice());
        sale.setSaleDate(LocalDateTime.now());

        Sales updatedSale = repository.save(sale);
        logger.info("Updated sale with id: {}", updatedSale.getSaleId());

        SaleResponseDto saleResponse = new SaleResponseDto(
            updatedSale.getSaleId(),
            updatedSale.getItemName(),
            updatedSale.getQuantity(),
            updatedSale.getPrice(),
            updatedSale.getSaleDate(),
            new CustomerResponseDto(
                updatedSale.getCustomer().getCustomerId(),
                updatedSale.getCustomer().getName(),
                updatedSale.getCustomer().getContactDetails().getPhoneNumber(),
                updatedSale.getCustomer().getContactDetails().getEmail(),
                updatedSale.getCustomer().getContactDetails().getAddress()
            )
        );
        logger.info("Exiting updateSale method with sale: {}", saleResponse);
        return saleResponse;
    }

    public List<SaleResponseDto> getSalesByCustomer(UUID customerId) { // Changed Long to UUID
        logger.info("Entering getSalesByCustomer method with customer id: {}", customerId);

        // Fetch the customer, throw exception if not found
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            logger.error("Customer with id {} not found", customerId);
            throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
        }

        // Fetch sales for the found customer
        List<SaleResponseDto> salesList = repository.findByCustomer(customer).stream()
                .map(sale -> new SaleResponseDto(
                    sale.getSaleId(),
                    sale.getItemName(),
                    sale.getQuantity(),
                    sale.getPrice(),
                    sale.getSaleDate(),
                    new CustomerResponseDto(
                        sale.getCustomer().getCustomerId(),
                        sale.getCustomer().getName(),
                        sale.getCustomer().getContactDetails().getPhoneNumber(),
                        sale.getCustomer().getContactDetails().getEmail(),
                        sale.getCustomer().getContactDetails().getAddress()
                    )
                ))
                .collect(Collectors.toList());

        logger.info("Fetched {} sales for customer with id: {}", salesList.size(), customerId);
        logger.info("Exiting getSalesByCustomer method");

        return salesList;
    }

    
    public void deleteSale(UUID id) { // Changed Long to UUID
        logger.info("Entering deleteSale method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));
        repository.delete(sale);
        logger.info("Deleted sale with id: {}", id);
        logger.info("Exiting deleteSale method");
    }


     /* total revenue grouped by item using a custom query.
     * Logs the process and returns a list of item names and their total revenue.
     */
    public List<Object[]> getTotalRevenuePerItem() {
        logger.info("Entering getTop5RevenuePerItem method");

        List<Object[]> totalRevenuePerItem = repository.findRevenuePerItem();

        if (totalRevenuePerItem.isEmpty()) {
            logger.info("No sales data available.");
            return List.of();
        }

        logger.info("Fetched total revenue for {} items", totalRevenuePerItem.size());

        List<Object[]> top5RevenuePerItem = totalRevenuePerItem.stream()
                .sorted(Comparator.comparingDouble((Object[] revenue) -> (Double) revenue[1]).reversed())
                .limit(5)
                .collect(Collectors.toList());

        logger.info("Exiting getTop5RevenuePerItem method");
        return top5RevenuePerItem;
    }


    
    public List<SalesMetricsDto> getRecentSales() {
        logger.info("Entering getRecentSales method with limit: 3");

        // Fetch recent sales with a fixed limit of 4
        List<Sales> recentSales = repository.findByOrderBySaleDateDesc(Pageable.ofSize(4)); // Limit is set here

        
        List<SalesMetricsDto> recentSalesSummary = recentSales.stream()
                .map(sale -> new SalesMetricsDto(
                        sale.getItemName(),
                        sale.getQuantity(),
                        sale.getPrice(),
                        sale.getSaleDate()  
                ))
                .collect(Collectors.toList());

        logger.info("Fetched {} recent sales", recentSalesSummary.size());
        logger.info("Exiting getRecentSales method");
        return recentSalesSummary;
    }
    





}
