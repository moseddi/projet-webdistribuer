package tn.esprit.stockservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    private String eventType;  // "PRODUCT_CREATED", "PRODUCT_DELETED", "STOCK_UPDATED"
    private Long productId;
    private String sku;
    private String name;
    private Integer quantityChange;  // positive for add, negative for remove
    private Integer newQuantity;
    private LocalDateTime timestamp;
}