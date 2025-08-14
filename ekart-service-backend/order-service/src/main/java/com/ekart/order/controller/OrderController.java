package com.ekart.order.controller;

import com.ekart.order.dto.CreateOrderDto;
import com.ekart.order.dto.OrderDto;
import com.ekart.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto,
                                               Authentication authentication) {
        try {
            String userId = authentication.getName();
            OrderDto order = orderService.createOrder(createOrderDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderId,
                                            Authentication authentication) {
        try {
            String userId = authentication.getName();
            OrderDto order = orderService.getOrderById(orderId, userId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getUserOrders(Authentication authentication, Pageable pageable) {
        try {
            String userId = authentication.getName();
            Page<OrderDto> orders = orderService.getUserOrders(userId, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching user orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable String orderId,
                                               Authentication authentication) {
        try {
            String userId = authentication.getName();
            OrderDto order = orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error cancelling order: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(@PathVariable String orderId,
                                                Authentication authentication) {
        try {
            String userId = authentication.getName();
            String status = orderService.getOrderStatus(orderId, userId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error fetching order status: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getOrderSummary(Authentication authentication) {
        try {
            String userId = authentication.getName();
            Object summary = orderService.getOrderSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching order summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
