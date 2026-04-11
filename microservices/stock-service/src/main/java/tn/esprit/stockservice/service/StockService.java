package tn.esprit.stockservice.service;

import tn.esprit.stockservice.dto.StockMovementRequest;
import tn.esprit.stockservice.dto.StockRequest;
import tn.esprit.stockservice.dto.StockResponse;

import java.util.List;

public interface StockService {

    // CRUD
    StockResponse createStock(StockRequest request);
    StockResponse getStockByProductId(Long productId);
    List<StockResponse> getAllStocks();
    StockResponse updateStock(Long productId, Integer quantity);
    void deleteStock(Long productId);

    // Stock movements
    StockResponse addStock(Long productId, Integer quantity, String supplier);
    StockResponse removeStock(Long productId, Integer quantity, String reference);
    StockResponse reserveStock(Long productId, Integer quantity, String orderRef);
    StockResponse releaseReservedStock(Long productId, Integer quantity, String orderRef);

    // Queries
    boolean checkAvailability(Long productId, Integer requestedQuantity);
    Integer getAvailableQuantity(Long productId);
    List<StockResponse> getLowStockItems();
    List<StockResponse> getOutOfStockItems();

    // Stock movement with event
    StockResponse processStockMovement(StockMovementRequest request);
}