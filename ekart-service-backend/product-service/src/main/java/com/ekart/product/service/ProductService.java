package com.ekart.product.service;

import com.ekart.product.dto.ProductDto;
import com.ekart.product.entity.Product;
import com.ekart.product.exception.ProductNotFoundException;
import com.ekart.product.repository.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "createProductFallback")
    @Retry(name = "product-service")
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating product: {}", productDto.getName());
        
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return convertToDto(savedProduct);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getAllProductsFallback")
    @Retry(name = "product-service")
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination");
        
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return products.map(this::convertToDto);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductByIdFallback")
    @Retry(name = "product-service")
    public ProductDto getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        return convertToDto(product);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductsByCategoryFallback")
    @Retry(name = "product-service")
    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        log.info("Fetching products by category: {}", category);
        
        Page<Product> products = productRepository.findByActiveTrueAndCategoryContainingIgnoreCase(category, pageable);
        return products.map(this::convertToDto);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "searchProductsFallback")
    @Retry(name = "product-service")
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        log.info("Searching products with query: {}", query);
        
        Page<Product> products = productRepository.searchProducts(query, pageable);
        return products.map(this::convertToDto);
    }

    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "updateProductFallback")
    @Retry(name = "product-service")
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        updateProductFields(existingProduct, productDto);
        Product updatedProduct = productRepository.save(existingProduct);
        
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return convertToDto(updatedProduct);
    }

    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "deleteProductFallback")
    @Retry(name = "product-service")
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        product.setActive(false);
        productRepository.save(product);
        
        log.info("Product deleted successfully with ID: {}", id);
    }

    @Transactional
    @CircuitBreaker(name = "product-service", fallbackMethod = "updateStockFallback")
    @Retry(name = "product-service")
    public ProductDto updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product ID: {} to quantity: {}", id, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        product.setStockQuantity(quantity);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Stock updated successfully for product ID: {}", id);
        return convertToDto(updatedProduct);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getLowStockProductsFallback")
    @Retry(name = "product-service")
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        log.info("Fetching low stock products with threshold: {}", threshold);
        
        List<Product> products = productRepository.findByActiveTrueAndStockQuantityLessThan(threshold);
        return products.stream().map(this::convertToDto).toList();
    }

    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        product.setImageUrl(dto.getImageUrl());
        product.setWeight(dto.getWeight());
        product.setDimensions(dto.getDimensions());
        product.setSku(dto.getSku());
        product.setDiscountPercentage(dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0);
        product.setTags(dto.getTags());
        product.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        return product;
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setImageUrl(product.getImageUrl());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setSku(product.getSku());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setTags(product.getTags());
        dto.setFeatured(product.getFeatured());
        dto.setActive(product.getActive());
        return dto;
    }

    private void updateProductFields(Product product, ProductDto dto) {
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStockQuantity() != null) product.setStockQuantity(dto.getStockQuantity());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getWeight() != null) product.setWeight(dto.getWeight());
        if (dto.getDimensions() != null) product.setDimensions(dto.getDimensions());
        if (dto.getSku() != null) product.setSku(dto.getSku());
        if (dto.getDiscountPercentage() != null) product.setDiscountPercentage(dto.getDiscountPercentage());
        if (dto.getTags() != null) product.setTags(dto.getTags());
        if (dto.getFeatured() != null) product.setFeatured(dto.getFeatured());
        if (dto.getActive() != null) product.setActive(dto.getActive());
    }

    // Fallback methods
    public ProductDto createProductFallback(ProductDto productDto, Exception ex) {
        log.error("Circuit breaker activated for product creation: {}", ex.getMessage());
        throw new RuntimeException("Product creation service is temporarily unavailable");
    }

    public Page<ProductDto> getAllProductsFallback(Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for get all products: {}", ex.getMessage());
        throw new RuntimeException("Product retrieval service is temporarily unavailable");
    }

    public ProductDto getProductByIdFallback(Long id, Exception ex) {
        log.error("Circuit breaker activated for get product by ID: {}", ex.getMessage());
        throw new RuntimeException("Product retrieval service is temporarily unavailable");
    }

    public Page<ProductDto> getProductsByCategoryFallback(String category, Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for get products by category: {}", ex.getMessage());
        throw new RuntimeException("Product retrieval service is temporarily unavailable");
    }

    public Page<ProductDto> searchProductsFallback(String query, Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for search products: {}", ex.getMessage());
        throw new RuntimeException("Product search service is temporarily unavailable");
    }

    public ProductDto updateProductFallback(Long id, ProductDto productDto, Exception ex) {
        log.error("Circuit breaker activated for product update: {}", ex.getMessage());
        throw new RuntimeException("Product update service is temporarily unavailable");
    }

    public void deleteProductFallback(Long id, Exception ex) {
        log.error("Circuit breaker activated for product deletion: {}", ex.getMessage());
        throw new RuntimeException("Product deletion service is temporarily unavailable");
    }

    public ProductDto updateStockFallback(Long id, Integer quantity, Exception ex) {
        log.error("Circuit breaker activated for stock update: {}", ex.getMessage());
        throw new RuntimeException("Stock update service is temporarily unavailable");
    }

    public List<ProductDto> getLowStockProductsFallback(Integer threshold, Exception ex) {
        log.error("Circuit breaker activated for low stock products: {}", ex.getMessage());
        throw new RuntimeException("Low stock products service is temporarily unavailable");
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getFeaturedProductsFallback")
    @Retry(name = "product-service")
    public Page<ProductDto> getFeaturedProducts(Pageable pageable) {
        log.info("Fetching featured products");
        
        Page<Product> products = productRepository.findByFeaturedTrueAndActiveTrue(pageable);
        return products.map(this::convertToDto);
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "getTopRatedProductsFallback")
    @Retry(name = "product-service")
    public Page<ProductDto> getTopRatedProducts(Pageable pageable) {
        log.info("Fetching top rated products");
        
        Page<Product> products = productRepository.findByActiveTrueOrderByAverageRatingDesc(pageable);
        return products.map(this::convertToDto);
    }

    // Fallback methods for new endpoints
    public Page<ProductDto> getFeaturedProductsFallback(Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for featured products: {}", ex.getMessage());
        throw new RuntimeException("Featured products service is temporarily unavailable");
    }

    public Page<ProductDto> getTopRatedProductsFallback(Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for top rated products: {}", ex.getMessage());
        throw new RuntimeException("Top rated products service is temporarily unavailable");
    }
}
