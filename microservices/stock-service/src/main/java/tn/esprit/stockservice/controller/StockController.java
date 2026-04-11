package tn.esprit.stockservice.controller;

import tn.esprit.stockservice.entity.Stock;
import tn.esprit.stockservice.dto.StockResponse;
import tn.esprit.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockRepository stockRepository;

    // Initialize stock for a product
    @PostMapping("/product/{productId}/init")
    public ResponseEntity<StockResponse> initStock(@PathVariable Long productId) {
        try {
            // Check if stock already exists
            Optional<Stock> existingStock = stockRepository.findByProductId(productId);
            if (existingStock.isPresent()) {
                return ResponseEntity.ok(mapToResponse(existingStock.get()));
            }

            // Create new stock with 0 quantity
            Stock stock = Stock.builder()
                    .productId(productId)
                    .quantity(0)
                    .reservedQuantity(0)
                    .availableQuantity(0)
                    .location("Default Warehouse")
                    .reorderLevel(5)
                    .reorderQuantity(10)
                    .lowStockAlert(false)
                    .build();

            Stock saved = stockRepository.save(stock);
            log.info("✅ Stock initialized for product: {} with 0 quantity", productId);
            return ResponseEntity.ok(mapToResponse(saved));
        } catch (Exception e) {
            log.error("Error initializing stock for product: {}", productId, e);
            return ResponseEntity.status(500).build();
        }
    }

    // Get available quantity
    @GetMapping("/product/{productId}/available")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable Long productId) {
        Optional<Stock> stockOpt = stockRepository.findByProductId(productId);
        if (stockOpt.isPresent()) {
            return ResponseEntity.ok(stockOpt.get().getAvailableQuantity());
        }
        return ResponseEntity.ok(0);
    }

    // Check availability
    @GetMapping("/product/{productId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Optional<Stock> stockOpt = stockRepository.findByProductId(productId);
        if (stockOpt.isPresent()) {
            return ResponseEntity.ok(stockOpt.get().getAvailableQuantity() >= quantity);
        }
        return ResponseEntity.ok(false);
    }

    // Add stock
    @PatchMapping("/product/{productId}/add")
    public ResponseEntity<StockResponse> addStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        try {
            Optional<Stock> stockOpt = stockRepository.findByProductId(productId);
            if (stockOpt.isEmpty()) {
                // Create stock first if it doesn't exist
                Stock newStock = Stock.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .reservedQuantity(0)
                        .availableQuantity(quantity)
                        .location("Default Warehouse")
                        .reorderLevel(5)
                        .reorderQuantity(10)
                        .lowStockAlert(quantity <= 5)
                        .build();

                Stock saved = stockRepository.save(newStock);
                log.info("✅ Stock created and added {} units for product: {}", quantity, productId);
                return ResponseEntity.ok(mapToResponse(saved));
            }

            Stock stock = stockOpt.get();
            stock.setQuantity(stock.getQuantity() + quantity);
            stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());

            // ✅ NULL CHECK FIX - line 93
            Integer reorderLevel = stock.getReorderLevel();
            if (reorderLevel != null) {
                stock.setLowStockAlert(stock.getAvailableQuantity() <= reorderLevel);
            } else {
                stock.setLowStockAlert(false);
                log.warn("⚠️ Reorder level is null for product: {}", productId);
            }

            Stock updated = stockRepository.save(stock);
            log.info("✅ Added {} units to product: {}. New available quantity: {}",
                    quantity, productId, stock.getAvailableQuantity());
            return ResponseEntity.ok(mapToResponse(updated));
        } catch (Exception e) {
            log.error("Error adding stock for product: {}", productId, e);
            return ResponseEntity.status(500).build();
        }
    }

    // Remove stock
    @PatchMapping("/product/{productId}/remove")
    public ResponseEntity<StockResponse> removeStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        try {
            Optional<Stock> stockOpt = stockRepository.findByProductId(productId);
            if (stockOpt.isEmpty()) {
                log.warn("⚠️ Stock not found for product: {}", productId);
                return ResponseEntity.badRequest().build();
            }

            Stock stock = stockOpt.get();
            if (stock.getAvailableQuantity() < quantity) {
                log.warn("⚠️ Insufficient stock for product: {}. Available: {}, Requested: {}",
                        productId, stock.getAvailableQuantity(), quantity);
                return ResponseEntity.badRequest().build();
            }

            stock.setQuantity(stock.getQuantity() - quantity);
            stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());

            // ✅ NULL CHECK FIX - line ~155
            Integer reorderLevel = stock.getReorderLevel();
            if (reorderLevel != null) {
                stock.setLowStockAlert(stock.getAvailableQuantity() <= reorderLevel);
            } else {
                stock.setLowStockAlert(false);
                log.warn("⚠️ Reorder level is null for product: {}", productId);
            }

            Stock updated = stockRepository.save(stock);
            log.info("✅ Removed {} units from product: {}. New available quantity: {}",
                    quantity, productId, stock.getAvailableQuantity());

            // Check if low stock alert is triggered
            if (stock.getLowStockAlert()) {
                log.warn("🔔 LOW STOCK ALERT for product: {}. Available: {}, Reorder level: {}",
                        productId, stock.getAvailableQuantity(), stock.getReorderLevel());
            }

            return ResponseEntity.ok(mapToResponse(updated));
        } catch (Exception e) {
            log.error("Error removing stock for product: {}", productId, e);
            return ResponseEntity.status(500).build();
        }
    }

    // Get stock by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<StockResponse> getStockByProductId(@PathVariable Long productId) {
        Optional<Stock> stockOpt = stockRepository.findByProductId(productId);
        if (stockOpt.isPresent()) {
            return ResponseEntity.ok(mapToResponse(stockOpt.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // Get all stocks
    @GetMapping
    public ResponseEntity<?> getAllStocks() {
        try {
            return ResponseEntity.ok(stockRepository.findAll());
        } catch (Exception e) {
            log.error("Error fetching all stocks", e);
            return ResponseEntity.status(500).build();
        }
    }

    // Map to response
    private StockResponse mapToResponse(Stock stock) {
        return StockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .quantity(stock.getQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(stock.getAvailableQuantity())
                .location(stock.getLocation())
                .reorderLevel(stock.getReorderLevel())
                .lowStockAlert(stock.getLowStockAlert())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}