package inventorymanagement.salesmodule.test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.inventorymanagement.salesmodule.dto.CustomerResponseDto;
import com.inventorymanagement.salesmodule.dto.SaleRequestDto;
import com.inventorymanagement.salesmodule.dto.SaleResponseDto;
import com.inventorymanagement.salesmodule.dto.SalesMetricsDto;
import com.inventorymanagement.salesmodule.dto.StockDTO;
import com.inventorymanagement.salesmodule.exception.CustomerNotFoundException;
import com.inventorymanagement.salesmodule.exception.InsufficientStockException;
import com.inventorymanagement.salesmodule.exception.SaleNotFoundException;
import com.inventorymanagement.salesmodule.feign.StockFeignClient;
import com.inventorymanagement.salesmodule.model.ContactDetails;
import com.inventorymanagement.salesmodule.model.Customer;
import com.inventorymanagement.salesmodule.model.Sales;
import com.inventorymanagement.salesmodule.repository.salesRepo;
import com.inventorymanagement.salesmodule.service.SaleService;
import com.inventorymanagement.salesmodule.service.customerService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class saleServiceTest {

    @Mock
    private salesRepo salesRepository;

    @Mock
    private customerService customerService;

    @Mock
    private StockFeignClient stockFeignClient;

    @InjectMocks
    private SaleService saleService;

    private Sales sale1;
    private Sales sale2;
    private Customer customer1;
    private ContactDetails contactDetails1;
    private SaleRequestDto saleRequestDto;
    private StockDTO stockDTO;
    private UUID saleId1;
    private UUID customerId1;

    @BeforeEach
    void setUp() {
        saleId1 = UUID.randomUUID();
        customerId1 = UUID.randomUUID();

        contactDetails1 = new ContactDetails();
        contactDetails1.setPhoneNumber("1234567890");
        contactDetails1.setEmail("test@example.com");
        contactDetails1.setAddress("Test Address");

        customer1 = new Customer();
        customer1.setCustomerId(customerId1);
        customer1.setName("Test Customer");
        customer1.setContactDetails(contactDetails1);

        sale1 = new Sales();
        sale1.setSaleId(saleId1);
        sale1.setItemName("Test Item");
        sale1.setQuantity(2);
        sale1.setPrice(10.0);
        sale1.setSaleDate(LocalDateTime.now());
        sale1.setCustomer(customer1);

        sale2 = new Sales();
        sale2.setSaleId(UUID.randomUUID());
        sale2.setItemName("Another Item");
        sale2.setQuantity(1);
        sale2.setPrice(25.0);
        sale2.setSaleDate(LocalDateTime.now().minusDays(1));
        sale2.setCustomer(customer1);

        saleRequestDto = new SaleRequestDto();
        saleRequestDto.setItemName("Test Item");
        saleRequestDto.setQuantity(2);
        saleRequestDto.setPrice(10.0);
        saleRequestDto.setCustomerName("Test Customer");
        saleRequestDto.setCustomerPhone("1234567890");
        saleRequestDto.setCustomerEmail("test@example.com");
        saleRequestDto.setCustomerAddress("Test Address");

        stockDTO = new StockDTO();
        stockDTO.setItemName("Test Item");
        stockDTO.setQuantity(10);
        stockDTO.setPrice(100.0);
    }

    @Test
    void getStockItemNames_success() {
        List<String> itemNames = Arrays.asList("Item A", "Item B");
        ResponseEntity<List<String>> responseEntity = new ResponseEntity<>(itemNames, HttpStatus.OK);
        when(stockFeignClient.getStockItemNames()).thenReturn(responseEntity);

        List<String> result = saleService.getStockItemNames();

        assertEquals(itemNames, result);
        verify(stockFeignClient, times(1)).getStockItemNames();
    }

    @Test
    void getAllSales_success() {
        when(salesRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2));

        List<SaleResponseDto> sales = saleService.getAllSales();

        assertEquals(2, sales.size());
        assertEquals(sale1.getSaleId(), sales.get(0).getSaleId());
        assertEquals(sale1.getItemName(), sales.get(0).getItemName());
        assertEquals(sale1.getQuantity(), sales.get(0).getQuantity());
        assertEquals(sale1.getPrice() * sale1.getQuantity(), sales.get(0).getPrice());
        assertEquals(sale1.getSaleDate(), sales.get(0).getSaleDate());
        assertEquals(sale1.getCustomer().getCustomerId(), sales.get(0).getCustomer().getCustomerId());
        verify(salesRepository, times(1)).findAll();
    }

    @Test
    void getSaleById_existingId_success() {
        when(salesRepository.findById(saleId1)).thenReturn(Optional.of(sale1));

        SaleResponseDto saleResponse = saleService.getSaleById(saleId1);

        assertEquals(sale1.getSaleId(), saleResponse.getSaleId());
        assertEquals(sale1.getItemName(), saleResponse.getItemName());
        assertEquals(sale1.getQuantity(), saleResponse.getQuantity());
        assertEquals(sale1.getPrice(), saleResponse.getPrice());
        assertEquals(sale1.getSaleDate(), saleResponse.getSaleDate());
        assertEquals(sale1.getCustomer().getCustomerId(), saleResponse.getCustomer().getCustomerId());
        verify(salesRepository, times(1)).findById(saleId1);
    }

    @Test
    void getSaleById_nonExistingId_throwsSaleNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(salesRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> saleService.getSaleById(nonExistingId));
        verify(salesRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void createSale_success() {
        List<String> itemNames = Arrays.asList("Test Item");
        ResponseEntity<List<String>> stockItemNamesResponse = new ResponseEntity<>(itemNames, HttpStatus.OK);
        when(stockFeignClient.getStockItemNames()).thenReturn(stockItemNamesResponse);
        ResponseEntity<Boolean> stockCheckResponse = new ResponseEntity<>(true, HttpStatus.OK);
        when(stockFeignClient.checkStockAvailability("Test Item", 2)).thenReturn(stockCheckResponse);
        ResponseEntity<StockDTO> stockDetailsResponse = new ResponseEntity<>(stockDTO, HttpStatus.OK);
        when(stockFeignClient.getStockDetails("Test Item")).thenReturn(stockDetailsResponse);
        doNothing().when(stockFeignClient).updateStockDetails("Test Item", 8, 80.0);
        when(customerService.getOrCreateCustomer(anyString(), anyString(), anyString(), anyString())).thenReturn(customer1);
        when(salesRepository.save(any(Sales.class))).thenReturn(sale1);

        SaleResponseDto createdSale = saleService.createSale(saleRequestDto);

        assertEquals(sale1.getSaleId(), createdSale.getSaleId());
        assertEquals(saleRequestDto.getItemName(), createdSale.getItemName());
        assertEquals(saleRequestDto.getQuantity(), createdSale.getQuantity());
        assertEquals(saleRequestDto.getPrice() * saleRequestDto.getQuantity(), createdSale.getPrice());
        assertEquals(customer1.getCustomerId(), createdSale.getCustomer().getCustomerId());
        verify(stockFeignClient, times(1)).getStockItemNames();
        verify(stockFeignClient, times(1)).checkStockAvailability("Test Item", 2);
        verify(stockFeignClient, times(1)).getStockDetails("Test Item");
        verify(stockFeignClient, times(1)).updateStockDetails("Test Item", 8, 80.0);
        verify(customerService, times(1)).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
        verify(salesRepository, times(1)).save(any(Sales.class));
    }

    @Test
    void createSale_itemNotAvailableInStock_throwsIllegalArgumentException() {
        List<String> itemNames = Arrays.asList("Another Item");
        ResponseEntity<List<String>> stockItemNamesResponse = new ResponseEntity<>(itemNames, HttpStatus.OK);
        when(stockFeignClient.getStockItemNames()).thenReturn(stockItemNamesResponse);

        assertThrows(IllegalArgumentException.class, () -> saleService.createSale(saleRequestDto));
        verify(stockFeignClient, times(1)).getStockItemNames();
        verify(stockFeignClient, never()).checkStockAvailability(anyString(), anyInt());
        verify(stockFeignClient, never()).getStockDetails(anyString());
        verify(stockFeignClient, never()).updateStockDetails(anyString(), anyInt(), anyDouble());
        verify(customerService, never()).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void createSale_insufficientStock_throwsInsufficientStockException() {
        List<String> itemNames = Arrays.asList("Test Item");
        ResponseEntity<List<String>> stockItemNamesResponse = new ResponseEntity<>(itemNames, HttpStatus.OK);
        when(stockFeignClient.getStockItemNames()).thenReturn(stockItemNamesResponse);
        ResponseEntity<Boolean> stockCheckResponse = new ResponseEntity<>(false, HttpStatus.OK);
        when(stockFeignClient.checkStockAvailability("Test Item", 2)).thenReturn(stockCheckResponse);

        assertThrows(InsufficientStockException.class, () -> saleService.createSale(saleRequestDto));
        verify(stockFeignClient, times(1)).getStockItemNames();
        verify(stockFeignClient, times(1)).checkStockAvailability("Test Item", 2);
        verify(stockFeignClient, never()).getStockDetails(anyString());
        verify(stockFeignClient, never()).updateStockDetails(anyString(), anyInt(), anyDouble());
        verify(customerService, never()).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void createSale_stockDetailsNotFound_throwsIllegalArgumentException() {
        List<String> itemNames = Arrays.asList("Test Item");
        ResponseEntity<List<String>> stockItemNamesResponse = new ResponseEntity<>(itemNames, HttpStatus.OK);
        when(stockFeignClient.getStockItemNames()).thenReturn(stockItemNamesResponse);
        ResponseEntity<Boolean> stockCheckResponse = new ResponseEntity<>(true, HttpStatus.OK);
        when(stockFeignClient.checkStockAvailability("Test Item", 2)).thenReturn(stockCheckResponse);
        ResponseEntity<StockDTO> stockDetailsResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(stockFeignClient.getStockDetails("Test Item")).thenReturn(stockDetailsResponse);

        assertThrows(IllegalArgumentException.class, () -> saleService.createSale(saleRequestDto));
        verify(stockFeignClient, times(1)).getStockItemNames();
        verify(stockFeignClient, times(1)).checkStockAvailability("Test Item", 2);
        verify(stockFeignClient, times(1)).getStockDetails("Test Item");
        verify(stockFeignClient, never()).updateStockDetails(anyString(), anyInt(), anyDouble());
        verify(customerService, never()).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
        verify(salesRepository, never()).save(any(Sales.class));
    }

//    @Test
//    void updateSale_existingId_success() {
//        UUID existingSaleId = UUID.randomUUID();
//        SaleRequestDto updateRequestDto = new SaleRequestDto();
//        updateRequestDto.setItemName("Updated Item");
//        updateRequestDto.setQuantity(3);
//        updateRequestDto.setPrice(15.0);
//        updateRequestDto.setCustomerName("Updated Customer");
//        updateRequestDto.setCustomerPhone("0987654321");
//        updateRequestDto.setCustomerEmail("updated@example.com");
//        updateRequestDto.setCustomerAddress("Updated Address");
//
//        Customer updatedCustomer = new Customer();
//        updatedCustomer.setCustomerId(UUID.randomUUID());
//        updatedCustomer.setName("Updated Customer");
//        updatedCustomer.setContactDetails(new ContactDetails("0987654321", "updated@example.com", "Updated Address"));
//
//        Sales existingSale = new Sales();
//        existingSale.setSaleId(existingSaleId);
//        existingSale.setItemName("Original Item");
//        existingSale.setQuantity(2);
//        existingSale.setPrice(10.0);
//        existingSale.setSaleDate(LocalDateTime.now());
//        existingSale.setCustomer(customer1);
//
//        Sales updatedSale = new Sales();
//        updatedSale.setSaleId(existingSaleId);
//        updatedSale.setItemName("Updated Item");
//        updatedSale.setQuantity(3);
//        updatedSale.setPrice(15.0);
//        updatedSale.setSaleDate(LocalDateTime.now());
//        updatedSale.setCustomer(updatedCustomer);
//
//        when(salesRepository.findById(existingSaleId)).thenReturn(Optional.of(existingSale));
//        when(customerService.getOrCreateCustomer(anyString(), anyString(), anyString(), anyString())).thenReturn(updatedCustomer);
//        when(salesRepository.save(any(Sales.class))).thenReturn(updatedSale);
//
//        SaleResponseDto result = saleService.updateSale(existingSaleId, updateRequestDto);
//
//        assertEquals(existingSaleId, result.getSaleId());
//        assertEquals(updateRequestDto.getItemName(), result.getItemName());
//        assertEquals(updateRequestDto.getQuantity(), result.getQuantity());
//        assertEquals(updateRequestDto.getPrice(), result.getPrice());
//        assertEquals(updatedCustomer.getCustomerId(), result.getCustomer().getCustomerId());
//        verify(salesRepository, times(1)).findById(existingSaleId);
//        verify(customerService, times(1)).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
//        verify(salesRepository, times(1)).save(any(Sales.class));
//    }

    @Test
    void updateSale_nonExistingId_throwsSaleNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(salesRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> saleService.updateSale(nonExistingId, saleRequestDto));
        verify(salesRepository, times(1)).findById(nonExistingId);
        verify(customerService, never()).getOrCreateCustomer(anyString(), anyString(), anyString(), anyString());
        verify(salesRepository, never()).save(any(Sales.class));
    }

    @Test
    void getSalesByCustomer_existingCustomerId_success() {
        when(customerService.getCustomerById(customerId1)).thenReturn(customer1);
        when(salesRepository.findByCustomer(customer1)).thenReturn(Arrays.asList(sale1, sale2));

        List<SaleResponseDto> sales = saleService.getSalesByCustomer(customerId1);

        assertEquals(2, sales.size());
        assertEquals(sale1.getSaleId(), sales.get(0).getSaleId());
        assertEquals(sale2.getSaleId(), sales.get(1).getSaleId());
        verify(customerService, times(1)).getCustomerById(customerId1);
        verify(salesRepository, times(1)).findByCustomer(customer1);
    }

    @Test
    void getSalesByCustomer_nonExistingCustomerId_throwsCustomerNotFoundException() {
        UUID nonExistingCustomerId = UUID.randomUUID();
        when(customerService.getCustomerById(nonExistingCustomerId)).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> saleService.getSalesByCustomer(nonExistingCustomerId));
        verify(customerService, times(1)).getCustomerById(nonExistingCustomerId);
        verify(salesRepository, never()).findByCustomer(any());
    }

    @Test
    void deleteSale_existingId_success() {
        when(salesRepository.findById(saleId1)).thenReturn(Optional.of(sale1));
        doNothing().when(salesRepository).delete(sale1);

        saleService.deleteSale(saleId1);

        verify(salesRepository, times(1)).findById(saleId1);
        verify(salesRepository, times(1)).delete(sale1);
    }

    @Test
    void deleteSale_nonExistingId_throwsSaleNotFoundException() {
        UUID nonExistingId = UUID.randomUUID();
        when(salesRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> saleService.deleteSale(nonExistingId));
        verify(salesRepository, times(1)).findById(nonExistingId);
        verify(salesRepository, never()).delete(any());
    }

}