package com.ekart.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "sagas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Saga {
    
    @Id
    private String id;
    
    private String orderId;
    
    private String userId;
    
    private SagaStatus status;
    
    private List<SagaStep> steps;
    
    private String currentStep;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum SagaStatus {
        STARTED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        COMPENSATING,
        COMPENSATED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SagaStep {
        private String stepName;
        private StepStatus status;
        private String compensationAction;
        private LocalDateTime executedAt;
        private String errorMessage;
        
        public enum StepStatus {
            PENDING,
            COMPLETED,
            FAILED,
            COMPENSATED
        }
    }
}
