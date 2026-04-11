package tn.esprit.stockservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {
    private Long productId;
    private Integer quantity;  // positive = add, negative = remove
    private String type;       // "SALE", "RESTOCK", "RESERVE", "RETURN"
    private String reference;  // Order number, etc.
    private String notes;
}