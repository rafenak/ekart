package com.ekart.payment.service;

import com.ekart.common.events.order.OrderCreatedEvent;
import com.ekart.common.events.payment.PaymentProcessedEvent;
import com.ekart.payment.dto.PaymentRequestDto;
import com.ekart.payment.dto.PaymentResponseDto;
import com.ekart.payment.entity.Payment;
import com.ekart.payment.gateway.PaymentGateway;
import com.ekart.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @CircuitBreaker(name = "payment-service", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-service")
    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing payment for order: {}", paymentRequest.getOrderId());
        
        String userId = getCurrentUserId();
        String paymentId = UUID.randomUUID().toString();
        
        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setUserId(userId);
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        
        payment = paymentRepository.save(payment);
        
        try {
            // Process payment through gateway
            PaymentGateway.PaymentResult result = paymentGateway.processPayment(paymentRequest);
            
            // Update payment status
            payment.setStatus(result.isSuccess() ? Payment.PaymentStatus.COMPLETED : Payment.PaymentStatus.FAILED);
            payment.setTransactionId(result.getTransactionId());
            payment.setGatewayResponse(result.getGatewayResponse());
            payment.setFailureReason(result.getFailureReason());
            
            payment = paymentRepository.save(payment);
            
            log.info("Payment processed successfully for order: {} with status: {}", 
                    paymentRequest.getOrderId(), payment.getStatus());
            
            return convertToDto(payment);
            
        } catch (Exception e) {
            log.error("Payment processing failed for order: {}", paymentRequest.getOrderId(), e);
            
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "order-created-topic")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order created event for order: {}", event.getOrderId());
        
        try {
            // Create payment request from order
            PaymentRequestDto paymentRequest = new PaymentRequestDto();
            paymentRequest.setOrderId(event.getOrderId());
            paymentRequest.setAmount(event.getTotalAmount());
            paymentRequest.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD); // Default
            
            // Create payment record
            Payment payment = new Payment();
            payment.setPaymentId(UUID.randomUUID().toString());
            payment.setOrderId(event.getOrderId());
            payment.setUserId(event.getUserId());
            payment.setAmount(event.getTotalAmount());
            payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
            payment.setStatus(Payment.PaymentStatus.PROCESSING);
            payment.setSagaId(event.getSagaId());
            
            payment = paymentRepository.save(payment);
            
            // Simulate payment processing
            PaymentGateway.PaymentResult result = paymentGateway.processPayment(paymentRequest);
            
            // Update payment status
            payment.setStatus(result.isSuccess() ? Payment.PaymentStatus.COMPLETED : Payment.PaymentStatus.FAILED);
            payment.setTransactionId(result.getTransactionId());
            payment.setGatewayResponse(result.getGatewayResponse());
            payment.setFailureReason(result.getFailureReason());
            
            payment = paymentRepository.save(payment);
            
            // Publish payment processed event
            PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent();
            paymentEvent.setEventId(UUID.randomUUID().toString());
            paymentEvent.setEventType("PAYMENT_PROCESSED");
            paymentEvent.setTimestamp(LocalDateTime.now());
            paymentEvent.setSagaId(event.getSagaId());
            paymentEvent.setUserId(event.getUserId());
            paymentEvent.setPaymentId(payment.getPaymentId());
            paymentEvent.setOrderId(event.getOrderId());
            paymentEvent.setAmount(payment.getAmount());
            paymentEvent.setPaymentMethod(payment.getPaymentMethod().name());
            paymentEvent.setStatus(payment.getStatus() == Payment.PaymentStatus.COMPLETED ? "SUCCESS" : "FAILED");
            paymentEvent.setTransactionId(payment.getTransactionId());
            
            kafkaTemplate.send("payment-processed-topic", paymentEvent);
            
            log.info("Payment processed and event published for order: {}", event.getOrderId());
            
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", event.getOrderId(), e);
            
            // Publish failed payment event
            PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent();
            paymentEvent.setEventId(UUID.randomUUID().toString());
            paymentEvent.setEventType("PAYMENT_PROCESSED");
            paymentEvent.setTimestamp(LocalDateTime.now());
            paymentEvent.setSagaId(event.getSagaId());
            paymentEvent.setUserId(event.getUserId());
            paymentEvent.setOrderId(event.getOrderId());
            paymentEvent.setAmount(event.getTotalAmount());
            paymentEvent.setStatus("FAILED");
            
            kafkaTemplate.send("payment-processed-topic", paymentEvent);
        }
    }

    @CircuitBreaker(name = "payment-service", fallbackMethod = "getPaymentByIdFallback")
    @Retry(name = "payment-service")
    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("Retrieving payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        
        return convertToDto(payment);
    }

    @CircuitBreaker(name = "payment-service", fallbackMethod = "getPaymentByOrderIdFallback")
    @Retry(name = "payment-service")
    public PaymentResponseDto getPaymentByOrderId(String orderId) {
        log.info("Retrieving payment for order: {}", orderId);
        
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        
        return convertToDto(payment);
    }

    @CircuitBreaker(name = "payment-service", fallbackMethod = "getUserPaymentsFallback")
    @Retry(name = "payment-service")
    public Page<PaymentResponseDto> getUserPayments(String userId, Pageable pageable) {
        log.info("Retrieving payments for user: {}", userId);
        
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(this::convertToDto);
    }

    @Transactional
    @CircuitBreaker(name = "payment-service", fallbackMethod = "refundPaymentFallback")
    @Retry(name = "payment-service")
    public PaymentResponseDto refundPayment(String paymentId) {
        log.info("Processing refund for payment: {}", paymentId);
        
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot refund payment that is not completed");
        }
        
        try {
            // Process refund through gateway
            boolean refundSuccess = paymentGateway.refundPayment(payment.getTransactionId());
            
            if (refundSuccess) {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
                payment = paymentRepository.save(payment);
                
                log.info("Refund processed successfully for payment: {}", paymentId);
            } else {
                throw new RuntimeException("Refund failed at gateway");
            }
            
            return convertToDto(payment);
            
        } catch (Exception e) {
            log.error("Refund processing failed for payment: {}", paymentId, e);
            throw new RuntimeException("Refund processing failed: " + e.getMessage());
        }
    }

    private PaymentResponseDto convertToDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrderId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setFailureReason(payment.getFailureReason());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setProcessedAt(payment.getProcessedAt());
        return dto;
    }

    private String getCurrentUserId() {
        // Extract user ID from JWT token
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Fallback methods
    public PaymentResponseDto processPaymentFallback(PaymentRequestDto paymentRequest, Exception ex) {
        log.error("Circuit breaker activated for payment processing: {}", ex.getMessage());
        throw new RuntimeException("Payment processing service is temporarily unavailable");
    }

    public PaymentResponseDto getPaymentByIdFallback(String paymentId, Exception ex) {
        log.error("Circuit breaker activated for get payment by ID: {}", ex.getMessage());
        throw new RuntimeException("Payment retrieval service is temporarily unavailable");
    }

    public PaymentResponseDto getPaymentByOrderIdFallback(String orderId, Exception ex) {
        log.error("Circuit breaker activated for get payment by order ID: {}", ex.getMessage());
        throw new RuntimeException("Payment retrieval service is temporarily unavailable");
    }

    public Page<PaymentResponseDto> getUserPaymentsFallback(String userId, Pageable pageable, Exception ex) {
        log.error("Circuit breaker activated for get user payments: {}", ex.getMessage());
        throw new RuntimeException("Payment retrieval service is temporarily unavailable");
    }

    public PaymentResponseDto refundPaymentFallback(String paymentId, Exception ex) {
        log.error("Circuit breaker activated for refund payment: {}", ex.getMessage());
        throw new RuntimeException("Refund processing service is temporarily unavailable");
    }
}
