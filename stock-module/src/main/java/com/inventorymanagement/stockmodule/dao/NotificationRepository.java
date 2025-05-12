package com.inventorymanagement.stockmodule.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventorymanagement.stockmodule.entity.Notification;
import com.inventorymanagement.stockmodule.entity.Stocks;

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
		void deleteByStocks_StockId(UUID stockId);
	
}