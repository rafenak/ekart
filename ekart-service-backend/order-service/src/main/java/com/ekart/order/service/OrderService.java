package com.ekart.order.service;

import com.ekart.order.dto.CreateOrderDto;
import com.ekart.order.dto.OrderDto;
import com.ekart.order.entity.Order;
import com.ekart.order.repository.OrderRepository;
import com.ekart.order.saga.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    public OrderDto createOrder(CreateOrderDto createOrderDto, String userId) {
        try {
            // Validate products and get details
            List<Order.OrderItem> orderItems = createOrderDto.getItems().stream()
                .map(item -> {
                    // In a real scenario, you would validate with product service
                    Order.OrderItem orderItem = new Order.OrderItem();
                    orderItem.setProductId(item.getProductId());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    // You would fetch the product name from product service
                    orderItem.setProductName("Product " + item.getProductId());
                    return orderItem;
                })
                .collect(Collectors.toList());

            // Calculate total amount
            BigDecimal totalAmount = orderItems.stream()
                .map(Order.OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create order
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            order.setUserId(userId);
            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);
            order.setStatus(Order.OrderStatus.PENDING);
            order.setShippingAddress(createOrderDto.getShippingAddress());
            order.setPaymentMethod(createOrderDto.getPaymentMethod());
            order.setSagaId(UUID.randomUUID().toString());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            // Save order
            Order savedOrder = orderRepository.save(order);

            // Start saga orchestration
            sagaOrchestrator.startOrderSaga(savedOrder);

            return convertToDto(savedOrder);
        } catch (Exception e) {
            log.error("Error creating order for user: {}", userId, e);
            throw new RuntimeException("Failed to create order", e);
        }
    }

    public OrderDto getOrderById(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return convertToDto(order);
    }

    public Page<OrderDto> getUserOrders(String userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToDto);
    }

    public OrderDto cancelOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        if (order.getStatus() != Order.OrderStatus.PENDING && 
            order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel order in current status");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    public String getOrderStatus(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        
        return order.getStatus().toString();
    }

    public Map<String, Object> getOrderSummary(String userId) {
        // Get all orders for user (we'll filter in memory for now)
        Page<Order> allUserOrders = orderRepository.findByUserId(userId, Pageable.unpaged());
        List<Order> userOrders = allUserOrders.getContent();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", userOrders.size());
        summary.put("pendingOrders", userOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
            .count());
        summary.put("completedOrders", userOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
            .count());
        summary.put("totalSpent", userOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return summary;
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setSagaId(order.getSagaId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        List<OrderDto.OrderItemDto> itemDtos = order.getItems().stream()
            .map(item -> {
                OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                itemDto.setProductId(item.getProductId());
                itemDto.setProductName(item.getProductName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                itemDto.setTotalPrice(item.getTotalPrice());
                return itemDto;
            })
            .collect(Collectors.toList());
        
        dto.setItems(itemDtos);
        return dto;
    }
}
