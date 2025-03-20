package inventorymanagement.salesmodule.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;

import inventorymanagement.salesmodule.model.Customer;
import inventorymanagement.salesmodule.model.Sales;
/**
 * Repository interface for the Sales entity, enabling CRUD operations and custom queries.
 * Supports finding sales by customer, ordering by sale date, and calculating revenue per item.
 */
public interface salesRepo extends JpaRepository<Sales, Long>{
	 List<Sales> findByCustomer(Customer customer);
	 
	
	 List<Sales> findByOrderBySaleDateDesc(Pageable pageable);
	 
	 @Query("SELECT s.itemName, SUM(s.price * s.quantity) FROM Sales s GROUP BY s.itemName")
	 List<Object[]> findRevenuePerItem();




}
