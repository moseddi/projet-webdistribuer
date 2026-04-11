package tn.esprit.stockservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;  // Links to Product in Product Service

    @Column(nullable = false)
    private Integer quantity;

    private Integer reservedQuantity = 0;

    private Integer availableQuantity;  // quantity - reservedQuantity

    private String location;  // Warehouse location

    private Integer reorderLevel = 5;  // Alert when stock below this

    private Integer reorderQuantity = 10;  // How many to order

    private Boolean lowStockAlert = false;

    private String supplier;

    private LocalDateTime lastRestocked;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}