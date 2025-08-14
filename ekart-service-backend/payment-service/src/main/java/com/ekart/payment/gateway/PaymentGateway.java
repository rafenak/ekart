package com.ekart.payment.gateway;

import com.ekart.payment.dto.PaymentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class PaymentGateway {
    
    private final Random random = new Random();
    
    public PaymentResult processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing payment through gateway for order: {}", paymentRequest.getOrderId());
        
        // Simulate processing time
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate payment processing based on payment method
        PaymentResult result = new PaymentResult();
        result.setTransactionId(UUID.randomUUID().toString());
        
        switch (paymentRequest.getPaymentMethod()) {
            case CREDIT_CARD:
                result = processCreditCardPayment(paymentRequest);
                break;
            case PAYPAL:
                result = processPayPalPayment(paymentRequest);
                break;
            case BANK_TRANSFER:
                result = processBankTransferPayment(paymentRequest);
                break;
            default:
                result.setSuccess(false);
                result.setFailureReason("Unsupported payment method");
        }
        
        log.info("Payment processing completed for order: {} with result: {}", 
                paymentRequest.getOrderId(), result.isSuccess() ? "SUCCESS" : "FAILED");
        
        return result;
    }
    
    private PaymentResult processCreditCardPayment(PaymentRequestDto paymentRequest) {
        PaymentResult result = new PaymentResult();
        result.setTransactionId("CC_" + UUID.randomUUID().toString().substring(0, 8));
        
        // Simulate 90% success rate
        if (random.nextDouble() < 0.9) {
            result.setSuccess(true);
            result.setGatewayResponse("Payment processed successfully");
        } else {
            result.setSuccess(false);
            result.setFailureReason("Insufficient funds");
            result.setGatewayResponse("Transaction declined by bank");
        }
        
        return result;
    }
    
    private PaymentResult processPayPalPayment(PaymentRequestDto paymentRequest) {
        PaymentResult result = new PaymentResult();
        result.setTransactionId("PP_" + UUID.randomUUID().toString().substring(0, 8));
        
        // Simulate 95% success rate
        if (random.nextDouble() < 0.95) {
            result.setSuccess(true);
            result.setGatewayResponse("PayPal payment successful");
        } else {
            result.setSuccess(false);
            result.setFailureReason("PayPal account restricted");
            result.setGatewayResponse("PayPal payment failed");
        }
        
        return result;
    }
    
    private PaymentResult processBankTransferPayment(PaymentRequestDto paymentRequest) {
        PaymentResult result = new PaymentResult();
        result.setTransactionId("BT_" + UUID.randomUUID().toString().substring(0, 8));
        
        // Simulate 85% success rate
        if (random.nextDouble() < 0.85) {
            result.setSuccess(true);
            result.setGatewayResponse("Bank transfer initiated successfully");
        } else {
            result.setSuccess(false);
            result.setFailureReason("Invalid account information");
            result.setGatewayResponse("Bank transfer failed");
        }
        
        return result;
    }
    
    public boolean refundPayment(String transactionId) {
        log.info("Processing refund for transaction: {}", transactionId);
        
        // Simulate refund processing time
        try {
            Thread.sleep(500 + random.nextInt(1000)); // 0.5-1.5 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% refund success rate
        boolean success = random.nextDouble() < 0.95;
        
        log.info("Refund processing completed for transaction: {} with result: {}", 
                transactionId, success ? "SUCCESS" : "FAILED");
        
        return success;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String gatewayResponse;
        private String failureReason;
    }
}
