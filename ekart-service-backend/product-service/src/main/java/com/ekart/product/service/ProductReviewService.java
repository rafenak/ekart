package com.ekart.product.service;

import com.ekart.product.dto.CreateReviewDto;
import com.ekart.product.dto.ProductReviewDto;
import com.ekart.product.entity.Product;
import com.ekart.product.entity.ProductReview;
import com.ekart.product.repository.ProductRepository;
import com.ekart.product.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ProductReviewDto createReview(Long productId, CreateReviewDto createReviewDto, String userId, String userName) {
        // Check if product exists
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }

        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new RuntimeException("User has already reviewed this product");
        }

        // Create review
        ProductReview review = new ProductReview();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setUserName(userName);
        review.setRating(createReviewDto.getRating());
        review.setComment(createReviewDto.getComment());
        review.setVerified(false); // Set based on purchase history
        review.setApproved(true); // Auto-approve for now
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        ProductReview savedReview = reviewRepository.save(review);

        // Update product rating statistics
        updateProductRatingStats(productId);

        return convertToDto(savedReview);
    }

    public Page<ProductReviewDto> getProductReviews(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = reviewRepository.findByProductIdAndApproved(productId, true, pageable);
        return reviews.map(this::convertToDto);
    }

    public ProductReviewDto getUserReview(Long productId, String userId) {
        ProductReview review = reviewRepository.findByProductIdAndUserId(productId, userId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        return convertToDto(review);
    }

    @Transactional
    public ProductReviewDto updateReview(Long reviewId, CreateReviewDto updateReviewDto, String userId) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this review");
        }

        review.setRating(updateReviewDto.getRating());
        review.setComment(updateReviewDto.getComment());
        review.setUpdatedAt(LocalDateTime.now());

        ProductReview savedReview = reviewRepository.save(review);

        // Update product rating statistics
        updateProductRatingStats(review.getProductId());

        return convertToDto(savedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, String userId) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this review");
        }

        Long productId = review.getProductId();
        reviewRepository.delete(review);

        // Update product rating statistics
        updateProductRatingStats(productId);
    }

    public Page<ProductReviewDto> getUserReviews(String userId, Pageable pageable) {
        Page<ProductReview> reviews = reviewRepository.findByUserId(userId, pageable);
        return reviews.map(this::convertToDto);
    }

    private void updateProductRatingStats(Long productId) {
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Integer reviewCount = reviewRepository.getReviewCountByProductId(productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount : 0);

        productRepository.save(product);
    }

    private ProductReviewDto convertToDto(ProductReview review) {
        ProductReviewDto dto = new ProductReviewDto();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setUserId(review.getUserId());
        dto.setUserName(review.getUserName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setVerified(review.getVerified());
        dto.setApproved(review.getApproved());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
