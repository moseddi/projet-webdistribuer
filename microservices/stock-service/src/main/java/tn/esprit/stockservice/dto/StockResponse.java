package tn.esprit.stockservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private String location;
    private Integer reorderLevel;
    private Boolean lowStockAlert;
    private String supplier;
    private LocalDateTime lastRestocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}