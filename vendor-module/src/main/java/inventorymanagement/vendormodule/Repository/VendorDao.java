package inventorymanagement.vendormodule.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import inventorymanagement.vendormodule.Entity.Vendor;

public interface VendorDao extends JpaRepository<Vendor,UUID> {
	List<Vendor> findByVendorNameContaining(String vendorName);
    

}
