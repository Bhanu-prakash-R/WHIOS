package inventorymanagement.salesmodule.Service;

import inventorymanagement.salesmodule.Dto.SaleRequestDto;
import inventorymanagement.salesmodule.Dto.SaleResponseDto;
import inventorymanagement.salesmodule.Dto.SalesMetricsDto;
import inventorymanagement.salesmodule.Dto.StockDTO;
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
import inventorymanagement.salesmodule.exception.CustomerNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

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
        logger.info("Entering createSale method with item: {}", saleRequestDto.getItemName());

        // Fetch stock items to validate availability
        ResponseEntity<List<String>> stockItemsResponse = stockFeignClient.getStockItemNames();
        List<String> stockItemNames = stockItemsResponse.getBody();

        if (stockItemNames == null || !stockItemNames.contains(saleRequestDto.getItemName())) {
            logger.error("Item '{}' is not available in stock. Sale cannot proceed.", saleRequestDto.getItemName());
            throw new IllegalArgumentException("Item not available in stock. Cannot proceed with sale.");
        }

        // Check stock availability
        ResponseEntity<Boolean> stockCheckResponse = stockFeignClient.checkStockAvailability(
                saleRequestDto.getItemName(), saleRequestDto.getQuantity()
        );

        if (!Boolean.TRUE.equals(stockCheckResponse.getBody())) {
            logger.info("Insufficient stock for item: {}", saleRequestDto.getItemName());
            throw new InsufficientStockException("Insufficient stock for item: " + saleRequestDto.getItemName());
        }

        // Fetch stock details
        ResponseEntity<StockDTO> stockDetailsResponse = stockFeignClient.getStockDetails(saleRequestDto.getItemName());
        StockDTO stockDetails = stockDetailsResponse.getBody();

        if (stockDetails == null) {
            logger.error("Stock details not found for item '{}'. Cannot proceed with sale.", saleRequestDto.getItemName());
            throw new IllegalArgumentException("Stock details not available for the given item.");
        }

        // Calculate remaining quantity and adjusted price
        int remainingQuantity = stockDetails.getQuantity() - saleRequestDto.getQuantity();
        if (remainingQuantity < 0) {
            logger.error("Sale quantity exceeds available stock for item '{}'.", saleRequestDto.getItemName());
            throw new IllegalArgumentException("Sale quantity exceeds available stock.");
        }

        double perItemPrice = stockDetails.getPrice() / stockDetails.getQuantity(); // Calculate price per unit
        logger.info("Per-item price calculated as: {}", perItemPrice);

        double newPrice = perItemPrice * remainingQuantity; // Calculate new total price
        logger.info("New price calculated for remaining stock: {}", newPrice);

        // Update stock in inventory with remaining quantity and new price
        logger.info("Updating stock with remainingQuantity: {}, newPrice: {}", remainingQuantity, newPrice);
        stockFeignClient.updateStockDetails(saleRequestDto.getItemName(), remainingQuantity, newPrice);

        // Create the sale entry
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
        sale.setPrice(saleRequestDto.getPrice()); // Use the price provided in the request
        sale.setSaleDate(LocalDateTime.now());

        Sales newSale = repository.save(sale);
        logger.info("Created new sale with id: {}", newSale.getSaleId());

        double totalPrice = newSale.getPrice() * newSale.getQuantity();

        // Build the response DTO
        SaleResponseDto saleResponse = new SaleResponseDto(
                newSale.getSaleId(),
                newSale.getItemName(),
                newSale.getQuantity(),
                totalPrice,
                newSale.getSaleDate(),
                new CustomerResponseDto(
                        newSale.getCustomer().getCustomerId(),
                        newSale.getCustomer().getName(),
                        newSale.getCustomer().getContactDetails().getPhoneNumber(),
                        newSale.getCustomer().getContactDetails().getEmail(),
                        sale.getCustomer().getContactDetails().getAddress()
                )
        );

        logger.info("Exiting createSale method with sale: {}", saleResponse);
        return saleResponse;
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

        // Fetch recent sales with a fixed limit of 3
        List<Sales> recentSales = repository.findByOrderBySaleDateDesc(Pageable.ofSize(3)); // Limit is set here

        
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
