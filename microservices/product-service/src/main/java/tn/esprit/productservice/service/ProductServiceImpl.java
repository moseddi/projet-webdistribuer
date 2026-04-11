package tn.esprit.productservice.service;

import tn.esprit.productservice.dto.ProductRequest;
import tn.esprit.productservice.dto.ProductResponse;
import tn.esprit.productservice.dto.ProductSearchCriteria;
import tn.esprit.productservice.entity.Product;
import tn.esprit.productservice.exception.ProductNotFoundException;
import tn.esprit.productservice.exception.DuplicateSkuException;
import tn.esprit.productservice.producer.ProductEventProducer;
import tn.esprit.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer eventProducer;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .brand(request.getBrand())
                .color(request.getColor())
                .size(request.getSize())
                .images(request.getImages())
                .mainImage(request.getMainImage())
                .active(request.getActive() != null ? request.getActive() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .discountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : 0)
                .viewCount(0)
                .soldCount(0)
                .rating(0.0)
                .build();

        Product savedProduct = productRepository.save(product);

        // ✅ Kafka with try-catch - won't crash if Kafka has issues
        try {
            eventProducer.sendProductCreatedEvent(savedProduct);
            log.info("✅ Kafka event sent for product: {}", savedProduct.getId());
        } catch (Exception e) {
            log.error("⚠️ Kafka event failed but product was saved: {}", e.getMessage());
        }

        log.info("Product created with ID: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getSku().equals(request.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setImages(request.getImages());
        product.setMainImage(request.getMainImage());

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        if (request.getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }
        if (request.getDiscountPercentage() != null) {
            product.setDiscountPercentage(request.getDiscountPercentage());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated with ID: {}", id);

        return mapToResponse(updatedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return mapToResponse(product);
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        log.info("Fetching product with SKU: {}", sku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");

        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);

        // ✅ Kafka with try-catch - won't crash if Kafka has issues
        try {
            eventProducer.sendProductDeletedEvent(id);
            log.info("✅ Kafka event sent for deleted product: {}", id);
        } catch (Exception e) {
            log.error("⚠️ Kafka event failed but product was deleted: {}", e.getMessage());
        }

        log.info("Product deleted with ID: {}", id);
    }

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        log.info("Searching products with criteria: {}", criteria);

        Pageable sortedPageable = createPageableWithSort(criteria, pageable);

        Page<Product> productPage = productRepository.searchProducts(
                criteria.getKeyword(),
                criteria.getCategory(),
                criteria.getBrand(),
                criteria.getColor(),
                criteria.getSize(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getActive(),
                criteria.getFeatured(),
                sortedPageable
        );

        return productPage.map(this::mapToResponse);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);

        return productRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        log.info("Fetching featured products");

        Pageable topSix = PageRequest.of(0, 6);
        return productRepository.findByFeaturedTrue(topSix).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getTopSellingProducts() {
        log.info("Fetching top selling products");

        return productRepository.findTop10ByOrderBySoldCountDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product ID: {} with quantity change: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
        }

        int oldQuantity = product.getQuantity();
        product.setQuantity(newQuantity);

        if (quantity < 0) {
            product.setSoldCount(product.getSoldCount() + Math.abs(quantity));
        }

        Product updatedProduct = productRepository.save(product);

        // ✅ SEND KAFKA EVENT FOR STOCK UPDATE
        try {
            eventProducer.sendStockUpdateEvent(id, quantity, newQuantity);
            log.info("✅ Kafka stock update event sent for product: {}, change: {}, old: {}, new: {}",
                    id, quantity, oldQuantity, newQuantity);
        } catch (Exception e) {
            log.error("⚠️ Kafka stock update event failed but product was updated: {}", e.getMessage());
        }

        return mapToResponse(updatedProduct);
    }

    @Override
    public boolean checkAvailability(Long id, Integer requestedQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return product.getActive() && product.getQuantity() >= requestedQuantity;
    }

    @Override
    public void incrementViewCount(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setViewCount(product.getViewCount() + 1);
            productRepository.save(product);
        });
    }

    @Override
    public void updateRating(Long id, Double newRating) {
        productRepository.findById(id).ifPresent(product -> {
            Double currentRating = product.getRating();
            if (currentRating == null || currentRating == 0) {
                product.setRating(newRating);
            } else {
                product.setRating((currentRating + newRating) / 2);
            }
            productRepository.save(product);
        });
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .brand(product.getBrand())
                .color(product.getColor())
                .size(product.getSize())
                .images(product.getImages())
                .mainImage(product.getMainImage())
                .active(product.getActive())
                .featured(product.getFeatured())
                .discountPercentage(product.getDiscountPercentage())
                .viewCount(product.getViewCount())
                .soldCount(product.getSoldCount())
                .rating(product.getRating())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private Pageable createPageableWithSort(ProductSearchCriteria criteria, Pageable pageable) {
        if (criteria.getSortBy() != null) {
            Sort.Direction direction = Sort.Direction.ASC;
            if ("DESC".equalsIgnoreCase(criteria.getSortDirection())) {
                direction = Sort.Direction.DESC;
            }
            Sort sort = Sort.by(direction, criteria.getSortBy());
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }
        return pageable;
    }
}