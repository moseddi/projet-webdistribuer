package tn.esprit.productservice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.productservice.client.StockServiceClient;
import tn.esprit.productservice.dto.ProductRequest;
import tn.esprit.productservice.dto.ProductResponse;
import tn.esprit.productservice.dto.ProductSearchCriteria;
import tn.esprit.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.env.Environment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final StockServiceClient stockServiceClient;  // ADD THIS
    @Autowired
    private Environment environment;

    // CREATE
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/instance-info")
    public ResponseEntity<Map<String, String>> getInstanceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("service", "product-service");
        info.put("port", environment.getProperty("local.server.port"));
        info.put("instance", UUID.randomUUID().toString());
        info.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(info);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        productService.incrementViewCount(id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // GET BY SKU
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // SEARCH
    @PostMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestBody ProductSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(criteria, pageable));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // GET FEATURED
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    // GET TOP SELLING
    @GetMapping("/top-selling")
    public ResponseEntity<List<ProductResponse>> getTopSellingProducts() {
        return ResponseEntity.ok(productService.getTopSellingProducts());
    }

    // UPDATE STOCK
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        ProductResponse response = productService.updateStock(id, quantity);
        return ResponseEntity.ok(response);
    }

    // CHECK AVAILABILITY
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        boolean available = productService.checkAvailability(id, quantity);
        return ResponseEntity.ok(available);
    }

    // UPDATE RATING
    @PatchMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long id,
            @RequestParam Double rating) {
        productService.updateRating(id, rating);
        return ResponseEntity.ok().build();
    }

    // NEW ENDPOINT FOR SYNC COMMUNICATION WITH STOCK SERVICE
    @GetMapping("/{id}/with-stock")
    public ResponseEntity<Map<String, Object>> getProductWithStock(@PathVariable Long id) {
        // Get product from Product Service
        ProductResponse product = productService.getProductById(id);

        // Call Stock Service SYNCHRONOUSLY via Feign Client
        Integer availableStock = 0;
        Boolean inStock = false;

        try {
            availableStock = stockServiceClient.getAvailableQuantity(id);
            inStock = stockServiceClient.checkStockAvailability(id, 1);
        } catch (Exception e) {
            // Stock service might be down - fallback
            availableStock = -1;
            inStock = false;
        }

        // Combine responses
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("availableStock", availableStock);
        response.put("inStock", inStock);
        response.put("message", inStock ? "Product in stock!" : "Product out of stock or service unavailable");

        return ResponseEntity.ok(response);
    }



}