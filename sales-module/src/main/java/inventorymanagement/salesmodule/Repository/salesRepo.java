package inventorymanagement.salesmodule.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inventorymanagement.salesmodule.model.Customer;
import inventorymanagement.salesmodule.model.Sales;

public interface salesRepo extends JpaRepository<Sales, Long>{
	 List<Sales> findByCustomer(Customer customer);

}
