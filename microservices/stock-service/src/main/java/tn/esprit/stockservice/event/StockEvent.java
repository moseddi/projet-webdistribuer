package tn.esprit.stockservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockEvent {
    private String eventType;      // "STOCK_CREATED", "STOCK_UPDATED", "LOW_STOCK", "OUT_OF_STOCK"
    private Long productId;
    private Integer quantity;
    private Integer availableQuantity;
    private String location;
    private LocalDateTime timestamp = LocalDateTime.now();

    public StockEvent(String eventType, Long productId, Integer quantity, Integer availableQuantity) {
        this.eventType = eventType;
        this.productId = productId;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }
}