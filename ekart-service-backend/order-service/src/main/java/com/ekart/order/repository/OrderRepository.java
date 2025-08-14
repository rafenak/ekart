package com.ekart.order.repository;

import com.ekart.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    List<Order> findByUserIdAndStatus(String userId, Order.OrderStatus status);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findBySagaId(String sagaId);
}
