package inventorymanagement.purchasemodule.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import inventorymanagement.purchasemodule.dto.PurchaseMetricsDto;
import inventorymanagement.purchasemodule.entity.Purchase;
//import inventorymanagement.salesmodule.model.Sales;


public interface PurchaseDao extends JpaRepository<Purchase, UUID> {

	/**
     * Finds purchases by vendor ID.
     *
     * @param vendorId The ID of the vendor.
     * @return A list of Purchase entities associated with the specified vendor ID.
     */
    List<Purchase> findByVendorId(int vendorId);

    /**
     * Finds purchases by vendor name.
     *
     * @param vendorName The name of the vendor.
     * @return A list of Purchase entities associated with the specified vendor name.
     */
    List<Purchase> findByVendorName(String vendorName);

    /**
     * Finds purchases by item name.
     *
     * @param itemName The name of the item.
     * @return A list of Purchase entities associated with the specified item name.
     */
    List<Purchase> findByItemName(String itemName);

    /**
     * Finds purchases by category.
     *
     * @param category The category of the item.
     * @return A list of Purchase entities associated with the specified category.
     */
    List<Purchase> findByCategory(String category);

   /* @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    List<Purchase> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT * FROM Purchase WHERE purchaseDate > :startDate", nativeQuery = true)
    List<Purchase> findRecentPurchases(@Param("startDate") LocalDateTime startDate);*/
    /**
     * Fetches the most recent purchases, limited to the top 3, and maps the results to PurchaseMetricsDto.
     *
     * @param pageable Controls the number of results and their order.
     * @return A list of PurchaseMetricsDto with fields like quantity, price, purchase date, and item name.
     *
     * Query Details:
     * - Selects specific fields (quantity, price, purchaseDate, itemName) from the Purchase entity.
     * - Orders the results by purchaseDate in descending order.
     * - Limits the results to the most recent 3 purchases.
     */
    
    @Query("SELECT new inventorymanagement.purchasemodule.dto.PurchaseMetricsDto("
    	     + " p.quantity, p.price, p.purchaseDate, "
    	     + " p.itemName) "
    	     + "FROM Purchase p ORDER BY p.purchaseDate DESC limit 3")
    	List<PurchaseMetricsDto> findRecentPurchases(Pageable pageable);
    
}
