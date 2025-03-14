package inventorymanagement.stockmodule.dao;

import inventorymanagement.stockmodule.entity.Notification;
import inventorymanagement.stockmodule.entity.Stocks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
	
	    List<Notification> findByStocks(Stocks stocks);
	
}