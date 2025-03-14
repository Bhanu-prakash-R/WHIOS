package inventorymanagement.purchasemodule.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import inventorymanagement.purchasemodule.entity.Purchase;


public interface PurchaseDao extends JpaRepository<Purchase, UUID> {

    List<Purchase> findByVendorId(int vendorId);

    List<Purchase> findByVendorName(String vendorName);

    List<Purchase> findByItemName(String itemName);

    List<Purchase> findByCategory(String category);

    @Query("SELECT p FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    List<Purchase> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT * FROM Purchase WHERE purchaseDate > :startDate", nativeQuery = true)
    List<Purchase> findRecentPurchases(@Param("startDate") LocalDateTime startDate);
}
