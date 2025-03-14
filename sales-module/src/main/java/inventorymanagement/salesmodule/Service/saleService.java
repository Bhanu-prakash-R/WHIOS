package inventorymanagement.salesmodule.Service;

import inventorymanagement.salesmodule.Dto.SaleRequestDto;
import inventorymanagement.salesmodule.Dto.SaleResponseDto;
import inventorymanagement.salesmodule.Dto.CustomerResponseDto;
import inventorymanagement.salesmodule.Repository.salesRepo;
//import com.sales.sales.client.StockFeignClient;
import inventorymanagement.salesmodule.exception.SaleNotFoundException;
import inventorymanagement.salesmodule.model.Customer;
import inventorymanagement.salesmodule.model.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class saleService {

    private static final Logger logger = LoggerFactory.getLogger(saleService.class);

    @Autowired
    private salesRepo repository;

    @Autowired
    private customerService customerService;
    


    

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

    public SaleResponseDto createSale(SaleRequestDto saleRequestDto) {
        logger.info("Entering createSale method with item: {}", saleRequestDto.getItemName());

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
        sale.setPrice(saleRequestDto.getPrice());
        sale.setSaleDate(LocalDateTime.now());

        Sales newSale = repository.save(sale);
        logger.info("Created new sale with id: {}", newSale.getSaleId());

        SaleResponseDto saleResponse = new SaleResponseDto(
            newSale.getSaleId(),
            newSale.getItemName(),
            newSale.getQuantity(),
            newSale.getPrice(),
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

    public SaleResponseDto updateSale(Long id, SaleRequestDto saleRequestDto) {
        logger.info("Entering updateSale method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));

        // Update customer details
        Customer customer = customerService.getOrCreateCustomer(
            saleRequestDto.getCustomerName(),
            saleRequestDto.getCustomerPhone(),
            saleRequestDto.getCustomerEmail(),
            saleRequestDto.getCustomerAddress()
        );
        sale.setCustomer(customer);

        // Update sale details
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

    public void deleteSale(Long id) {
        logger.info("Entering deleteSale method with id: {}", id);
        Sales sale = repository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + id));
        repository.delete(sale);
        logger.info("Deleted sale with id: {}", id);
        logger.info("Exiting deleteSale method");
    }
}
