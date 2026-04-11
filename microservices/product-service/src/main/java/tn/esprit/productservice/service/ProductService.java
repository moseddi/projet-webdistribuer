package tn.esprit.productservice.service;

import tn.esprit.productservice.dto.ProductRequest;
import tn.esprit.productservice.dto.ProductResponse;
import tn.esprit.productservice.dto.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    // Basic CRUD
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);
    List<ProductResponse> getAllProducts();
    void deleteProduct(Long id);

    // Advanced features
    Page<ProductResponse> searchProducts(ProductSearchCriteria criteria, Pageable pageable);
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> getFeaturedProducts();
    List<ProductResponse> getTopSellingProducts();

    // Stock management
    ProductResponse updateStock(Long id, Integer quantity);
    boolean checkAvailability(Long id, Integer requestedQuantity);

    // Views and stats
    void incrementViewCount(Long id);
    void updateRating(Long id, Double newRating);
}