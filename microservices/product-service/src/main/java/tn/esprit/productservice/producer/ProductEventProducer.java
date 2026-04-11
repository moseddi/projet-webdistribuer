package tn.esprit.productservice.producer;

import tn.esprit.productservice.entity.Product;
import tn.esprit.productservice.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public void sendProductCreatedEvent(Product product) {
        ProductEvent event = ProductEvent.builder()
                .eventType("PRODUCT_CREATED")
                .productId(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("product-events", event);
        log.info("✅ Product created event sent for product: {}", product.getId());
    }

    public void sendProductDeletedEvent(Long productId) {
        ProductEvent event = ProductEvent.builder()
                .eventType("PRODUCT_DELETED")
                .productId(productId)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("product-events", event);
        log.info("✅ Product deleted event sent for product: {}", productId);
    }

    public void sendStockUpdateEvent(Long productId, Integer quantityChange, Integer newQuantity) {
        ProductEvent event = ProductEvent.builder()
                .eventType("STOCK_UPDATED")
                .productId(productId)
                .quantityChange(quantityChange)
                .newQuantity(newQuantity)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("product-events", event);
        log.info("📤 Stock update event sent - Product: {}, Change: {}, New Qty: {}",
                productId, quantityChange, newQuantity);
    }
}