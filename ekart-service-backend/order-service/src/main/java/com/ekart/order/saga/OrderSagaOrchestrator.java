package com.ekart.order.saga;

import com.ekart.common.events.order.OrderCreatedEvent;
import com.ekart.common.events.payment.PaymentProcessedEvent;
import com.ekart.common.events.notification.NotificationEvent;
import com.ekart.order.entity.Order;
import com.ekart.order.entity.Saga;
import com.ekart.order.repository.OrderRepository;
import com.ekart.order.repository.SagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaOrchestrator {

    private final SagaRepository sagaRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void startOrderSaga(Order order) {
        log.info("Starting order saga for order: {}", order.getId());
        
        String sagaId = UUID.randomUUID().toString();
        
        Saga saga = new Saga();
        saga.setId(sagaId);
        saga.setOrderId(order.getId());
        saga.setUserId(order.getUserId());
        saga.setStatus(Saga.SagaStatus.STARTED);
        saga.setCurrentStep("ORDER_CREATED");
        saga.setSteps(Arrays.asList(
            new Saga.SagaStep("ORDER_CREATED", Saga.SagaStep.StepStatus.COMPLETED, "CANCEL_ORDER", LocalDateTime.now(), null),
            new Saga.SagaStep("PAYMENT_PROCESSING", Saga.SagaStep.StepStatus.PENDING, "REFUND_PAYMENT", null, null),
            new Saga.SagaStep("NOTIFICATION_SENT", Saga.SagaStep.StepStatus.PENDING, "SEND_CANCELLATION_NOTIFICATION", null, null)
        ));
        
        sagaRepository.save(saga);
        
        // Update order with saga ID
        order.setSagaId(sagaId);
        order.setStatus(Order.OrderStatus.PAYMENT_PROCESSING);
        orderRepository.save(order);
        
        // Publish order created event
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setEventId(UUID.randomUUID().toString());
        orderCreatedEvent.setEventType("ORDER_CREATED");
        orderCreatedEvent.setTimestamp(LocalDateTime.now());
        orderCreatedEvent.setSagaId(sagaId);
        orderCreatedEvent.setUserId(order.getUserId());
        orderCreatedEvent.setOrderId(order.getId());
        orderCreatedEvent.setTotalAmount(order.getTotalAmount());
        orderCreatedEvent.setItems(order.getItems().stream()
            .map(item -> new OrderCreatedEvent.OrderItem(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
            ))
            .toList());
        orderCreatedEvent.setShippingAddress(order.getShippingAddress());
        
        kafkaTemplate.send("order-created-topic", orderCreatedEvent);
        
        // Update saga status
        saga.setStatus(Saga.SagaStatus.IN_PROGRESS);
        saga.setCurrentStep("PAYMENT_PROCESSING");
        sagaRepository.save(saga);
        
        log.info("Order saga started successfully for order: {}", order.getId());
    }

    @KafkaListener(topics = "payment-processed-topic")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Received payment processed event for saga: {}", event.getSagaId());
        
        Optional<Saga> sagaOpt = sagaRepository.findById(event.getSagaId());
        if (sagaOpt.isEmpty()) {
            log.error("Saga not found: {}", event.getSagaId());
            return;
        }
        
        Saga saga = sagaOpt.get();
        Optional<Order> orderOpt = orderRepository.findById(saga.getOrderId());
        if (orderOpt.isEmpty()) {
            log.error("Order not found: {}", saga.getOrderId());
            return;
        }
        
        Order order = orderOpt.get();
        
        if ("SUCCESS".equals(event.getStatus())) {
            // Payment successful - continue saga
            log.info("Payment successful for order: {}", order.getId());
            
            // Update order status
            order.setStatus(Order.OrderStatus.PAYMENT_COMPLETED);
            orderRepository.save(order);
            
            // Update saga step
            updateSagaStep(saga, "PAYMENT_PROCESSING", Saga.SagaStep.StepStatus.COMPLETED);
            saga.setCurrentStep("NOTIFICATION_SENT");
            sagaRepository.save(saga);
            
            // Send notification event
            NotificationEvent notificationEvent = new NotificationEvent();
            notificationEvent.setEventId(UUID.randomUUID().toString());
            notificationEvent.setEventType("ORDER_CONFIRMATION");
            notificationEvent.setTimestamp(LocalDateTime.now());
            notificationEvent.setSagaId(saga.getId());
            notificationEvent.setUserId(order.getUserId());
            notificationEvent.setOrderId(order.getId());
            notificationEvent.setType("EMAIL");
            notificationEvent.setSubject("Order Confirmation");
            notificationEvent.setMessage("Your order has been confirmed and payment processed successfully.");
            
            kafkaTemplate.send("notification-topic", notificationEvent);
            
        } else {
            // Payment failed - start compensation
            log.error("Payment failed for order: {}", order.getId());
            
            order.setStatus(Order.OrderStatus.PAYMENT_FAILED);
            orderRepository.save(order);
            
            updateSagaStep(saga, "PAYMENT_PROCESSING", Saga.SagaStep.StepStatus.FAILED);
            saga.setStatus(Saga.SagaStatus.COMPENSATING);
            sagaRepository.save(saga);
            
            // Start compensation
            compensateOrder(saga);
        }
    }

    @KafkaListener(topics = "notification-sent-topic")
    public void handleNotificationSent(NotificationEvent event) {
        log.info("Received notification sent event for saga: {}", event.getSagaId());
        
        Optional<Saga> sagaOpt = sagaRepository.findById(event.getSagaId());
        if (sagaOpt.isEmpty()) {
            log.error("Saga not found: {}", event.getSagaId());
            return;
        }
        
        Saga saga = sagaOpt.get();
        
        // Update saga step
        updateSagaStep(saga, "NOTIFICATION_SENT", Saga.SagaStep.StepStatus.COMPLETED);
        saga.setStatus(Saga.SagaStatus.COMPLETED);
        saga.setCurrentStep("COMPLETED");
        sagaRepository.save(saga);
        
        log.info("Order saga completed successfully for saga: {}", saga.getId());
    }

    private void compensateOrder(Saga saga) {
        log.info("Starting compensation for saga: {}", saga.getId());
        
        // Cancel order
        Optional<Order> orderOpt = orderRepository.findById(saga.getOrderId());
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            // Send cancellation notification
            NotificationEvent notificationEvent = new NotificationEvent();
            notificationEvent.setEventId(UUID.randomUUID().toString());
            notificationEvent.setEventType("ORDER_CANCELLED");
            notificationEvent.setTimestamp(LocalDateTime.now());
            notificationEvent.setSagaId(saga.getId());
            notificationEvent.setUserId(order.getUserId());
            notificationEvent.setOrderId(order.getId());
            notificationEvent.setType("EMAIL");
            notificationEvent.setSubject("Order Cancelled");
            notificationEvent.setMessage("Your order has been cancelled due to payment failure.");
            
            kafkaTemplate.send("notification-topic", notificationEvent);
        }
        
        // Update saga status
        saga.setStatus(Saga.SagaStatus.COMPENSATED);
        sagaRepository.save(saga);
        
        log.info("Compensation completed for saga: {}", saga.getId());
    }

    private void updateSagaStep(Saga saga, String stepName, Saga.SagaStep.StepStatus status) {
        saga.getSteps().stream()
            .filter(step -> step.getStepName().equals(stepName))
            .findFirst()
            .ifPresent(step -> {
                step.setStatus(status);
                step.setExecutedAt(LocalDateTime.now());
            });
        saga.setUpdatedAt(LocalDateTime.now());
    }
}
