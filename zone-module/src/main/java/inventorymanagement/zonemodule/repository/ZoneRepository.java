package inventorymanagement.zonemodule.repository;

import inventorymanagement.zonemodule.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, String> { // Changed ID type from Long to UUID
    List<Zone> findByIsActiveTrue();
    Long countByIsActiveTrue();
}
