package inventorymanagement.zonemodule.repository;

import inventorymanagement.zonemodule.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Zone entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and query capabilities.
 */
@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> { // Using String as ID type instead of UUID for simplicity

    /**
     * Retrieves all zones that are currently active.
     * 
     * @return A list of Zone entities with an active status (`isActive = true`).
     */
    List<Zone> findByIsActiveTrue();

    /**
     * Counts the total number of zones that are currently active.
     * 
     * @return A Long value representing the count of active zones (`isActive = true`).
     */
    Long countByIsActiveTrue();
}
