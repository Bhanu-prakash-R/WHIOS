package inventorymanagement.stockmodule.dao;

import inventorymanagement.stockmodule.entity.Stocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Stocks entities.
 *
 * Provides methods for performing database operations on Stocks, 
 * including custom queries to filter by quantity and retrieve specific data.
 */
public interface StocksRepository extends JpaRepository<Stocks, UUID> {
	/**
     * Finds stocks with quantity less than the given threshold.
     *
     * @param threshold The quantity threshold.
     * @return List of stocks with quantity below the threshold.
     */
	@Query("SELECT s FROM Stocks s WHERE s.quantity < :threshold")
    List<Stocks> findByQuantityLessThan(@Param("threshold") int threshold);
	/**
     * Finds a stock by its item name.
     *
     * @param itemName The name of the item.
     * @return Optional containing the stock if found, or empty otherwise.
     */
    Optional<Stocks> findByItemName(String itemName);
   // Optional<Stocks> findByItemName2(String itemName);
    /**
     * Retrieves the item names and quantities of all stocks.
     *
     * @return List of object arrays containing item names and their quantities.
     */
    
    @Query("SELECT s.itemName, s.quantity FROM Stocks s")
    List<Object[]> findItemNameAndQuantity();

    
    /*@Query("SELECT s.itemName, SUM(s.quantity) FROM Stock s GROUP BY s.itemName")
    List<Object[]> findItemNameAndQuantity();*/

    
   // List<Stocks> findByQuantityLess(int quantity);
}
