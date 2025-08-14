package com.ekart.product.controller;

import com.ekart.common.dto.ApiResponse;
import com.ekart.product.dto.CreateReviewDto;
import com.ekart.product.dto.ProductDto;
import com.ekart.product.dto.ProductReviewDto;
import com.ekart.product.service.ProductReviewService;
import com.ekart.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductReviewService productReviewService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Create product request: {}", productDto.getName());
        
        ProductDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdProduct, "Product created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Get all products request - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        log.info("Get product by ID request: {}", id);
        
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Get products by category request: {}", category);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Search products request: {}", query);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductDto> products = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductDto productDto) {
        
        log.info("Update product request for ID: {}", id);
        
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("Delete product request for ID: {}", id);
        
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @PathVariable Long id, 
            @RequestParam Integer quantity) {
        
        log.info("Update stock request for product ID: {} to quantity: {}", id, quantity);
        
        ProductDto updatedProduct = productService.updateStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(updatedProduct, "Stock updated successfully"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        log.info("Get low stock products request with threshold: {}", threshold);
        
        List<ProductDto> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(products, "Low stock products retrieved successfully"));
    }

    // Review endpoints
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ProductReviewDto>> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewDto createReviewDto,
            @RequestParam String userId,
            @RequestParam String userName) {
        
        log.info("Create review request for product: {} by user: {}", productId, userId);
        
        ProductReviewDto review = productReviewService.createReview(productId, createReviewDto, userId, userName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Review created successfully"));
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Page<ProductReviewDto>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get reviews for product: {}", productId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReviewDto> reviews = productReviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Reviews retrieved successfully"));
    }

    @GetMapping("/{productId}/reviews/user")
    public ResponseEntity<ApiResponse<ProductReviewDto>> getUserReview(
            @PathVariable Long productId,
            @RequestParam String userId) {
        
        log.info("Get user review for product: {} by user: {}", productId, userId);
        
        ProductReviewDto review = productReviewService.getUserReview(productId, userId);
        return ResponseEntity.ok(ApiResponse.success(review, "User review retrieved successfully"));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ProductReviewDto>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewDto updateReviewDto,
            @RequestParam String userId) {
        
        log.info("Update review: {} by user: {}", reviewId, userId);
        
        ProductReviewDto review = productReviewService.updateReview(reviewId, updateReviewDto, userId);
        return ResponseEntity.ok(ApiResponse.success(review, "Review updated successfully"));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam String userId) {
        
        log.info("Delete review: {} by user: {}", reviewId, userId);
        
        productReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Review deleted successfully"));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get featured products request");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending());
        Page<ProductDto> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Featured products retrieved successfully"));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getTopRatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get top rated products request");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("averageRating").descending());
        Page<ProductDto> products = productService.getTopRatedProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products, "Top rated products retrieved successfully"));
    }
}
