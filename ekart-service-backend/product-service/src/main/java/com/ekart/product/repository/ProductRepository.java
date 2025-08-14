package com.ekart.product.repository;

import com.ekart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    Page<Product> findByActiveTrueAndCategoryContainingIgnoreCase(String category, Pageable pageable);
    
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);
    
    List<Product> findByActiveTrueAndStockQuantityLessThan(Integer threshold);
    
    Optional<Product> findByIdAndActiveTrue(Long id);
    
    Page<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);
    
    Page<Product> findByActiveTrueOrderByAverageRatingDesc(Pageable pageable);
}
