package tn.esprit.stockservice.service;

import tn.esprit.stockservice.client.ProductServiceClient;
import tn.esprit.stockservice.dto.StockMovementRequest;
import tn.esprit.stockservice.dto.StockRequest;
import tn.esprit.stockservice.dto.StockResponse;
import tn.esprit.stockservice.entity.Stock;
import tn.esprit.stockservice.event.StockEvent;
import tn.esprit.stockservice.exception.StockNotFoundException;
import tn.esprit.stockservice.producer.StockEventProducer;
import tn.esprit.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductServiceClient productServiceClient;
    private final StockEventProducer stockEventProducer;

    @Override
    public StockResponse createStock(StockRequest request) {
        log.info("Creating stock for product ID: {}", request.getProductId());

        // Verify product exists (SYNCHRONOUS CALL)
        try {
            productServiceClient.getProductById(request.getProductId());
        } catch (Exception e) {
            throw new RuntimeException("Product not found with ID: " + request.getProductId());
        }

        Stock stock = Stock.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .reservedQuantity(0)
                .availableQuantity(request.getQuantity())
                .location(request.getLocation())
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 5)
                .reorderQuantity(request.getReorderQuantity() != null ? request.getReorderQuantity() : 10)
                .supplier(request.getSupplier())
                .lowStockAlert(false)
                .lastRestocked(LocalDateTime.now())
                .build();

        Stock savedStock = stockRepository.save(stock);

        // Send ASYNCHRONOUS event
        StockEvent event = new StockEvent("STOCK_CREATED", savedStock.getProductId(),
                savedStock.getQuantity(), savedStock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        return mapToResponse(savedStock);
    }

    @Override
    public StockResponse getStockByProductId(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));
        return mapToResponse(stock);
    }

    @Override
    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StockResponse updateStock(Long productId, Integer quantity) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));

        stock.setQuantity(quantity);
        stock.setAvailableQuantity(quantity - stock.getReservedQuantity());
        stock.setLowStockAlert(stock.getAvailableQuantity() <= stock.getReorderLevel());

        Stock updatedStock = stockRepository.save(stock);

        // Send ASYNCHRONOUS event
        StockEvent event = new StockEvent("STOCK_UPDATED", productId, quantity, stock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        return mapToResponse(updatedStock);
    }

    @Override
    public void deleteStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));
        stockRepository.delete(stock);

        // Send ASYNCHRONOUS event
        StockEvent event = new StockEvent("STOCK_DELETED", productId, 0, 0);
        stockEventProducer.sendStockEvent(event);
    }

    @Override
    public StockResponse addStock(Long productId, Integer quantity, String supplier) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));

        stock.setQuantity(stock.getQuantity() + quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setSupplier(supplier != null ? supplier : stock.getSupplier());
        stock.setLastRestocked(LocalDateTime.now());
        stock.setLowStockAlert(stock.getAvailableQuantity() <= stock.getReorderLevel());

        Stock updatedStock = stockRepository.save(stock);

        // SYNC call to Product Service to update product quantity
        try {
            productServiceClient.updateProductStock(productId, stock.getQuantity());
        } catch (Exception e) {
            log.error("Failed to update product service synchronously", e);
        }

        // ASYNC event
        StockEvent event = new StockEvent("STOCK_RESTOCKED", productId, quantity, stock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        return mapToResponse(updatedStock);
    }

    @Override
    public StockResponse removeStock(Long productId, Integer quantity, String reference) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + stock.getAvailableQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());
        stock.setLowStockAlert(stock.getAvailableQuantity() <= stock.getReorderLevel());

        Stock updatedStock = stockRepository.save(stock);

        // SYNC call to Product Service
        try {
            productServiceClient.updateProductStock(productId, stock.getQuantity());
        } catch (Exception e) {
            log.error("Failed to update product service synchronously", e);
        }

        // ASYNC event
        StockEvent event = new StockEvent("STOCK_SOLD", productId, -quantity, stock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        // Check low stock
        if (stock.getLowStockAlert()) {
            StockEvent lowStockEvent = new StockEvent("LOW_STOCK", productId,
                    stock.getQuantity(), stock.getAvailableQuantity());
            stockEventProducer.sendStockEvent(lowStockEvent);
        }

        return mapToResponse(updatedStock);
    }

    @Override
    public StockResponse reserveStock(Long productId, Integer quantity, String orderRef) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Cannot reserve " + quantity + ". Available: " + stock.getAvailableQuantity());
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());

        Stock updatedStock = stockRepository.save(stock);

        StockEvent event = new StockEvent("STOCK_RESERVED", productId, quantity, stock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        return mapToResponse(updatedStock);
    }

    @Override
    public StockResponse releaseReservedStock(Long productId, Integer quantity, String orderRef) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for product: " + productId));

        stock.setReservedQuantity(stock.getReservedQuantity() - quantity);
        stock.setAvailableQuantity(stock.getQuantity() - stock.getReservedQuantity());

        Stock updatedStock = stockRepository.save(stock);

        StockEvent event = new StockEvent("STOCK_RELEASED", productId, quantity, stock.getAvailableQuantity());
        stockEventProducer.sendStockEvent(event);

        return mapToResponse(updatedStock);
    }

    @Override
    public boolean checkAvailability(Long productId, Integer requestedQuantity) {
        Stock stock = stockRepository.findByProductId(productId).orElse(null);
        return stock != null && stock.getAvailableQuantity() >= requestedQuantity;
    }

    @Override
    public Integer getAvailableQuantity(Long productId) {
        return stockRepository.findByProductId(productId)
                .map(Stock::getAvailableQuantity)
                .orElse(0);
    }

    @Override
    public List<StockResponse> getLowStockItems() {
        return stockRepository.findLowStockItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockResponse> getOutOfStockItems() {
        return stockRepository.findOutOfStockItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StockResponse processStockMovement(StockMovementRequest request) {
        switch (request.getType()) {
            case "SALE":
                return removeStock(request.getProductId(), request.getQuantity(), request.getReference());
            case "RESTOCK":
                return addStock(request.getProductId(), request.getQuantity(), request.getNotes());
            case "RESERVE":
                return reserveStock(request.getProductId(), request.getQuantity(), request.getReference());
            case "RETURN":
                return addStock(request.getProductId(), request.getQuantity(), "RETURN: " + request.getReference());
            default:
                throw new RuntimeException("Unknown movement type: " + request.getType());
        }
    }

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
                .supplier(stock.getSupplier())
                .lastRestocked(stock.getLastRestocked())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}