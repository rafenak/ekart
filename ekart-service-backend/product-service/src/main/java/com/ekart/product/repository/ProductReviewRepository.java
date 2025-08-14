package com.ekart.product.repository;

import com.ekart.product.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    Page<ProductReview> findByProductIdAndApproved(Long productId, Boolean approved, Pageable pageable);
    
    List<ProductReview> findByProductIdAndApproved(Long productId, Boolean approved);
    
    Optional<ProductReview> findByProductIdAndUserId(Long productId, String userId);
    
    boolean existsByProductIdAndUserId(Long productId, String userId);
    
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.productId = :productId AND pr.approved = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.productId = :productId AND pr.approved = true")
    Integer getReviewCountByProductId(@Param("productId") Long productId);
    
    Page<ProductReview> findByUserId(String userId, Pageable pageable);
}
