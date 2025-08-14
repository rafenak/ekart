package com.ekart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String brand;
    
    private String imageUrl;
    
    private Double averageRating;
    
    private Integer reviewCount;
    
    private Double weight;
    
    private String dimensions;
    
    private String sku;
    
    private Double discountPercentage;
    
    private String tags;
    
    private Boolean featured;
    
    private Boolean active;
}
