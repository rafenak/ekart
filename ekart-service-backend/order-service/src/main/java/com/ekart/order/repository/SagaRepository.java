package com.ekart.order.repository;

import com.ekart.order.entity.Saga;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SagaRepository extends MongoRepository<Saga, String> {
    
    Optional<Saga> findByOrderId(String orderId);
    
    List<Saga> findByStatus(Saga.SagaStatus status);
    
    List<Saga> findByUserId(String userId);
}
