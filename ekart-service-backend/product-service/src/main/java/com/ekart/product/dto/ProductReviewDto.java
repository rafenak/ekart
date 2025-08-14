package com.ekart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDto {
    
    private Long id;
    private Long productId;
    private String userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Boolean verified;
    private Boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
