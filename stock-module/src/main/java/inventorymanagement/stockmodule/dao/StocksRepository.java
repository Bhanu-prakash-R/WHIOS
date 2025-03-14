package inventorymanagement.stockmodule.dao;

import inventorymanagement.stockmodule.entity.Stocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StocksRepository extends JpaRepository<Stocks, UUID> {
	@Query("SELECT s FROM Stocks s WHERE s.quantity < :threshold")
    List<Stocks> findByQuantityLessThan(@Param("threshold") int threshold);
}
