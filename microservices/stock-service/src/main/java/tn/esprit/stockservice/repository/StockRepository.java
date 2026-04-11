package tn.esprit.stockservice.repository;

import tn.esprit.stockservice.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    List<Stock> findByLowStockAlertTrue();

    @Query("SELECT s FROM Stock s WHERE s.availableQuantity <= s.reorderLevel")
    List<Stock> findLowStockItems();

    @Query("SELECT s FROM Stock s WHERE s.availableQuantity = 0")
    List<Stock> findOutOfStockItems();

    @Query("SELECT s FROM Stock s WHERE s.location = :location")
    List<Stock> findByLocation(@Param("location") String location);
}