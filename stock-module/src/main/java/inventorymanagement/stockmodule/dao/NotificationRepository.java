package inventorymanagement.stockmodule.dao;

import inventorymanagement.stockmodule.entity.Notification;
import inventorymanagement.stockmodule.entity.Stocks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Notification entities.
 *
 * Provides methods to perform database operations on Notification entities,
 * including a custom method to find notifications by associated stock.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
	
	/**
     * Finds notifications associated with the given stock.
     *
     * @param stocks The stock entity to filter notifications by.
     * @return List of notifications associated with the given stock.
     */
	    List<Notification> findByStocks(Stocks stocks);
	    Optional<Notification> findByStocksAndMessage(Stocks stocks, String message);
	
}