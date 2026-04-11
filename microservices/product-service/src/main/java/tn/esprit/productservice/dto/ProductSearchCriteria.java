package tn.esprit.productservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchCriteria {
    private String keyword;
    private String category;
    private String brand;
    private String color;
    private String size;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;
    private Boolean featured;
    private String sortBy;      // price, name, createdAt, rating
    private String sortDirection; // ASC or DESC
}