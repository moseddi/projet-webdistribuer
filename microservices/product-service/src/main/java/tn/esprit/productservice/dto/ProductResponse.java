package tn.esprit.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
    private String brand;
    private String color;
    private String size;
    private String images;
    private String mainImage;
    private Boolean active;
    private Boolean featured;
    private Integer discountPercentage;
    private Integer viewCount;
    private Integer soldCount;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Calculated field
    public BigDecimal getDiscountedPrice() {
        if (discountPercentage != null && discountPercentage > 0) {
            return price.multiply(BigDecimal.valueOf(1 - discountPercentage / 100.0));
        }
        return price;
    }
}