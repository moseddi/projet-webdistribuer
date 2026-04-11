package tn.esprit.stockservice.consumer;

import tn.esprit.stockservice.entity.Stock;
import tn.esprit.stockservice.event.ProductEvent;
import tn.esprit.stockservice.producer.StockEventProducer;
import tn.esprit.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private final StockRepository stockRepository;
    private final StockEventProducer stockEventProducer;

    @KafkaListener(topics = "product-events", groupId = "stock-group")
    public void consumeProductEvent(ProductEvent event) {
        log.info("📨 Received product event: {}", event);

        switch (event.getEventType()) {
            case "PRODUCT_CREATED":
                Stock stock = Stock.builder()
                        .productId(event.getProductId())
                        .quantity(0)
                        .availableQuantity(0)
                        .reorderLevel(5)
                        .reorderQuantity(10)
                        .reservedQuantity(0)
                        .lowStockAlert(false)
                        .build();
                stockRepository.save(stock);
                log.info("✅ Stock created for product: {}", event.getProductId());
                break;

            case "PRODUCT_DELETED":
                stockRepository.findByProductId(event.getProductId())
                        .ifPresent(stockRepository::delete);
                log.info("✅ Stock deleted for product: {}", event.getProductId());
                break;

            case "STOCK_UPDATED":
                handleStockUpdated(event);
                break;

            default:
                log.warn("⚠️ Unknown event type: {}", event.getEventType());
        }
    }

    private void handleStockUpdated(ProductEvent event) {
        log.info("📦 Processing stock update for product: {}, change: {}",
                event.getProductId(), event.getQuantityChange());

        Optional<Stock> stockOpt = stockRepository.findByProductId(event.getProductId());

        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            Integer quantityChange = event.getQuantityChange();

            if (quantityChange != null) {
                int newQuantity = stock.getQuantity() + quantityChange;

                if (newQuantity >= 0) {
                    stock.setQuantity(newQuantity);
                    stock.setAvailableQuantity(newQuantity - stock.getReservedQuantity());

                    // Null check for reorderLevel
                    Integer reorderLevel = stock.getReorderLevel();
                    if (reorderLevel != null) {
                        stock.setLowStockAlert(stock.getAvailableQuantity() <= reorderLevel);
                    } else {
                        stock.setLowStockAlert(false);
                        log.warn("⚠️ Reorder level is null for product: {}", event.getProductId());
                    }

                    stockRepository.save(stock);
                    log.info("✅ Stock updated: product {} - new quantity: {}, available: {}",
                            event.getProductId(), stock.getQuantity(), stock.getAvailableQuantity());

                    if (stock.getLowStockAlert()) {
                        log.warn("🔔 LOW STOCK ALERT for product {}: only {} units available",
                                event.getProductId(), stock.getAvailableQuantity());
                    }
                } else {
                    log.error("❌ Cannot update stock: would be negative for product {}", event.getProductId());
                }
            } else {
                log.warn("⚠️ Quantity change is null for product: {}", event.getProductId());
            }
        } else {
            log.warn("⚠️ Stock not found for product: {}", event.getProductId());
        }
    }
}