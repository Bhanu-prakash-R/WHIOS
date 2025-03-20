package inventorymanagement.salesmodule.Service;

import inventorymanagement.salesmodule.Dto.SaleRequestDto;
import inventorymanagement.salesmodule.Dto.SaleResponseDto;
import inventorymanagement.salesmodule.Dto.SalesMetricsDto;
import inventorymanagement.salesmodule.Dto.CustomerResponseDto;
import inventorymanagement.salesmodule.Repository.salesRepo;
import inventorymanagement.salesmodule.exception.InsufficientStockException;
//import com.sales.sales.client.StockFeignClient;
import inventorymanagement.salesmodule.exception.SaleNotFoundException;
import inventorymanagement.salesmodule.feign.StockFeignClient;
import inventorymanagement.salesmodule.model.Customer;
import inventorymanagement.salesmodule.model.Sales;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class saleService {

    private static final Logger logger = LoggerFactory.getLogger(saleService.class);

    @Autowired
    private salesRepo repository;

    @Autowired
    private customerService customerService;
    
    @Autowired
    private StockFeignClient stockFeignClient;
    
    /**
     * Fetches a list of stock item names using the StockFeignClient.
     * Returns the response body containing the list of item names.
     */
    public List<String> getStockItemNames() {
        return stockFeignClient.getStockItemNames().getBody();
    }
    
    public List<SaleResponseDto> getAllSales() {
        logger.info("Entering getAllSales method");
        List<SaleResponseDto> salesList = repository.findAll().stream()
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
                        sale.getCustomer().getContactDetails().getEmail()
                    )
                ))
                .collect(Collectors.toList());
        logger.info("Fetched {} sales", salesList.size());
        logger.info("Exiting getAllSales method");
        return salesList;
    }

    public SaleResponseDto getSaleById(Long id) {
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
                sale.getCustomer().getContactDetails().getEmail()
            )
        );
        logger.info("Exiting getSaleById method with sale: {}", saleResponse);
        return saleResponse;
    }
    /**
     * Creates a new Sale based on the provided SaleRequestDto.
     * - Retrieves or creates customer details.
     * - Validates stock availability via StockFeignClient.
     * - Throws InsufficientStockException if stock is unavailable.
     */
    @Transactional
    public SaleResponseDto createSale(SaleRequestDto saleRequestDto) {
        logger.info("Entering createSale method with item: {}", saleRequestDto.getItemName());
 
        ResponseEntity<List<String>> stockItemsResponse = stockFeignClient.getStockItemNames();
        List<String> stockItemNames = stockItemsResponse.getBody();

        if (stockItemNames == null || !stockItemNames.contains(saleRequestDto.getItemName())) {
            logger.error("Item '{}' is not available in stock. Sale cannot proceed.", saleRequestDto.getItemName());
            throw new IllegalArgumentException("Item not available in stock. Cannot proceed with sale.");
        }
        // Retrieve or create the customer details
        Customer customer = customerService.getOrCreateCustomer(
                saleRequestDto.getCustomerName(),
                saleRequestDto.getCustomerPhone(),
                saleRequestDto.getCustomerEmail(),
                saleRequestDto.getCustomerAddress()
        );

        // Check stock availability before creating the sale
        ResponseEntity<Boolean> stockCheckResponse = stockFeignClient.checkStockAvailability(
                saleRequestDto.getItemName(), saleRequestDto.getQuantity()
        );

        if (!Boolean.TRUE.equals(stockCheckResponse.getBody())) {
            logger.info("Insufficient stock for item: {}", saleRequestDto.getItemName());
            throw new InsufficientStockException("Insufficient stock for item: " + saleRequestDto.getItemName());
        }

        /**
         *  Create and save the sale entity
         */
        Sales sale = new Sales();
        sale.setCustomer(customer);
        sale.setItemName(saleRequestDto.getItemName());
        sale.setQuantity(saleRequestDto.getQuantity());
        sale.setPrice(saleRequestDto.getPrice()); // Set the price per item
        sale.setSaleDate(LocalDateTime.now());

        /**
         * Save sale entity in the repository
         */
        Sales newSale = repository.save(sale);
        logger.info("Created new sale with id: {}", newSale.getSaleId());

        /**
         * Update stock quantity via Stock Service
         */
        stockFeignClient.updateStockQuantity(saleRequestDto.getItemName(), saleRequestDto.getQuantity());

        /**
         *  Create SaleResponseDto with total price (price per item * quantity)
         *  // Calculate total price
         */
        double totalPrice = newSale.getPrice() * newSale.getQuantity(); 
        
        SaleResponseDto saleResponse = new SaleResponseDto(
                newSale.getSaleId(),
                newSale.getItemName(),
                newSale.getQuantity(),
                totalPrice, // Set total price in the "price" field
                newSale.getSaleDate(),
                new CustomerResponseDto(
                        newSale.getCustomer().getCustomerId(),
                        newSale.getCustomer().getName(),
                        newSale.getCustomer().getContactDetails().getPhoneNumber(),
                        newSale.getCustomer().getContactDetails().getEmail()
                )
        );

        logger.info("Exiting createSale method with sale: {}", saleResponse);
        return saleResponse;
    }

    /**
     * Updates an existing Sale by its ID based on the provided SaleRequestDto.
     * Throws SaleNotFoundException if the sale is not found in the database.
     */

    public SaleResponseDto updateSale(Long id, SaleRequestDto saleRequestDto) {
        logger.info("Entering updateSale method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));

        /**
         *  Update customer details
         */
        Customer customer = customerService.getOrCreateCustomer(
            saleRequestDto.getCustomerName(),
            saleRequestDto.getCustomerPhone(),
            saleRequestDto.getCustomerEmail(),
            saleRequestDto.getCustomerAddress()
        );
        sale.setCustomer(customer);

        /**
         *  Update sale details
         */
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
                updatedSale.getCustomer().getContactDetails().getEmail()
            )
        );
        logger.info("Exiting updateSale method with sale: {}", saleResponse);
        return saleResponse;
    }
    /**
     * Retrieves sales for a specific customer by their ID and converts them into SaleResponseDto objects.
     * Logs the process and returns the list of sales with mapped customer details.
     */
    public List<SaleResponseDto> getSalesByCustomer(Long customerId) {
        logger.info("Entering getSalesByCustomer method with customer id: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
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
                        sale.getCustomer().getContactDetails().getEmail()
                    )
                ))
                .collect(Collectors.toList());
        logger.info("Fetched {} sales for customer with id: {}", salesList.size(), customerId);
        logger.info("Exiting getSalesByCustomer method");
        return salesList;
    }
    
    /**
     * Deletes a sale by its ID.
     * Throws SaleNotFoundException if the sale is not found.
     * Logs the process of finding and deleting the sale.
     */
    public void deleteSale(Long id) {
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
        logger.info("Entering getTotalRevenuePerItem method");

        // Fetch total revenue grouped by item
        List<Object[]> totalRevenuePerItem = repository.findRevenuePerItem();

        logger.info("Fetched total revenue for {} items", totalRevenuePerItem.size());
        logger.info("Exiting getTotalRevenuePerItem method");
        return totalRevenuePerItem;
    }


    
    public List<SalesMetricsDto> getRecentSales() {
        logger.info("Entering getRecentSales method with limit: 3");

        // Fetch recent sales with a fixed limit of 3
        List<Sales> recentSales = repository.findByOrderBySaleDateDesc(Pageable.ofSize(3)); // Limit is set here

        // Map to SalesDTO format (itemName, quantity, price, saleDate)
        List<SalesMetricsDto> recentSalesSummary = recentSales.stream()
                .map(sale -> new SalesMetricsDto(
                        sale.getItemName(),
                        sale.getQuantity(),
                        sale.getPrice(),
                        sale.getSaleDate()  // Include saleDate here
                ))
                .collect(Collectors.toList());

        logger.info("Fetched {} recent sales", recentSalesSummary.size());
        logger.info("Exiting getRecentSales method");
        return recentSalesSummary;
    }





}
