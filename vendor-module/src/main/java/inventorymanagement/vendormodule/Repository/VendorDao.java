package inventorymanagement.vendormodule.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import inventorymanagement.vendormodule.Entity.Vendor;

/**
 * Repository interface for performing database operations on the Vendor entity.
 * Extends JpaRepository to provide CRUD operations and additional query capabilities.
 */
public interface VendorDao extends JpaRepository<Vendor, UUID> {

    /**
     * Custom method to search for vendors whose names contain the specified keyword.
     * 
     * @param vendorName The keyword to search for within vendor names.
     * @return A list of Vendor entities whose names contain the specified keyword.
     */
    List<Vendor> findByVendorNameContaining(String vendorName);
}
