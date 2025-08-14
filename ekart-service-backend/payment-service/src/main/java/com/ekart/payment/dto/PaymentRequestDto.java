package com.ekart.payment.dto;

import com.ekart.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;
    
    private String currency = "USD";
    
    // Payment method specific fields
    private CreditCardDto creditCard;
    private PayPalDto paypal;
    private BankTransferDto bankTransfer;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditCardDto {
        @NotBlank(message = "Card number is required")
        private String cardNumber;
        
        @NotBlank(message = "Expiry month is required")
        private String expiryMonth;
        
        @NotBlank(message = "Expiry year is required")
        private String expiryYear;
        
        @NotBlank(message = "CVV is required")
        private String cvv;
        
        @NotBlank(message = "Cardholder name is required")
        private String cardholderName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayPalDto {
        @NotBlank(message = "PayPal email is required")
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankTransferDto {
        @NotBlank(message = "Account number is required")
        private String accountNumber;
        
        @NotBlank(message = "Routing number is required")
        private String routingNumber;
        
        @NotBlank(message = "Account holder name is required")
        private String accountHolderName;
    }
}
