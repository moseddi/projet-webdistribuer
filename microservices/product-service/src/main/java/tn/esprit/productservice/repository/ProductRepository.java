package tn.esprit.productservice.repository;

import tn.esprit.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByCategory(String category);

    Page<Product> findByFeaturedTrue(Pageable pageable);

    List<Product> findTop10ByOrderBySoldCountDesc();

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:color IS NULL OR p.color = :color) AND " +
            "(:size IS NULL OR p.size = :size) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:active IS NULL OR p.active = :active) AND " +
            "(:featured IS NULL OR p.featured = :featured)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("category") String category,
                                 @Param("brand") String brand,
                                 @Param("color") String color,
                                 @Param("size") String size,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("active") Boolean active,
                                 @Param("featured") Boolean featured,
                                 Pageable pageable);
}