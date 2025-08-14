# 🏗️ Complete E-Kart Microservices Architecture

## 🎯 Overview

This is a **production-ready** Spring Boot microservices ecosystem featuring:

- ✅ **8 Microservices** with distinct responsibilities
- ✅ **Security** with Keycloak OAuth2/OpenID Connect
- ✅ **Fault Tolerance** with Circuit Breaker, Retry, Bulkhead
- ✅ **Saga Pattern** for distributed transactions
- ✅ **Multiple Databases** (PostgreSQL, MongoDB, MySQL, Redis)
- ✅ **Event-Driven Architecture** with Kafka
- ✅ **Service Discovery** with Eureka
- ✅ **API Gateway** with routing and security
- ✅ **Comprehensive Testing** scripts

## 🏛️ Architecture Diagram

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   Port: 8080    │
                    └─────────┬───────┘
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
    ┌────▼────┐         ┌─────▼─────┐        ┌─────▼─────┐
    │ Config  │         │  Eureka   │        │ Keycloak  │
    │ Server  │         │  Server   │        │   Auth    │
    │  :8888  │         │   :8761   │        │   :8090   │
    └─────────┘         └───────────┘        └───────────┘
                              │
    ┌─────────────────────────┼─────────────────────────┐
    │                         │                         │
┌───▼───┐ ┌─────────┐ ┌───────▼──┐ ┌─────────────┐ ┌─────────────┐
│ User  │ │Product  │ │  Order   │ │  Payment    │ │Notification │
│Service│ │Service  │ │ Service  │ │  Service    │ │  Service    │
│ :8081 │ │ :8082   │ │  :8083   │ │   :8084     │ │   :8085     │
└───┬───┘ └────┬────┘ └─────┬────┘ └──────┬──────┘ └──────┬──────┘
    │          │            │             │               │
┌───▼───┐  ┌───▼────┐  ┌────▼─────┐  ┌───▼─────┐    ┌────▼────┐
│Postgres│ │Postgres│  │ MongoDB  │  │ MySQL   │    │ Redis   │
│(Users) │ │(Products│  │ (Orders) │  │(Payments│    │(Notify) │
└────────┘ └─────────┘  └──────────┘  └─────────┘    └─────────┘
```

## 📦 Services Overview

| Service | Port | Database | Purpose | Key Features |
|---------|------|----------|---------|--------------|
| **API Gateway** | 8080 | - | Entry point, routing, security | JWT validation, Rate limiting, CORS |
| **Config Server** | 8888 | - | Centralized configuration | Git-based config, Environment profiles |
| **Eureka Server** | 8761 | - | Service discovery | Health monitoring, Load balancing |
| **User Service** | 8081 | PostgreSQL | User management, Authentication | Keycloak integration, Role-based access |
| **Product Service** | 8082 | PostgreSQL | Product catalog | CRUD operations, Search, Inventory |
| **Order Service** | 8083 | MongoDB | Order management, Saga orchestration | Distributed transactions, Event sourcing |
| **Payment Service** | 8084 | MySQL | Payment processing | Multiple payment methods, Refunds |
| **Notification Service** | 8085 | Redis | Multi-channel notifications | Email, SMS, Push, In-app |

## 🚀 Quick Start

### Prerequisites
```bash
# Required software
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- curl and jq (for testing)
```

### 1. Start Infrastructure
```bash
# Start all databases and external services
docker-compose up -d

# Wait for services to be ready (2-3 minutes)
```

### 2. Configure Authentication
```bash
# Setup Keycloak realm, client, and users
./setup-keycloak.sh
```

### 3. Start All Microservices
```bash
# Start all services in correct order
./start-services.sh
```

### 4. Test the Complete System
```bash
# Run comprehensive API tests
./test-complete-apis.sh
```

### 5. Stop Everything
```bash
# Stop all services and cleanup
./stop-services.sh
```

## 🔐 Security Implementation

### Authentication Flow
1. **User Registration** → Creates user in both Keycloak and local DB
2. **User Login** → Authenticates with Keycloak, returns JWT
3. **API Requests** → JWT validated at API Gateway
4. **Service Communication** → Secure inter-service calls

### Security Features
- **OAuth2/OpenID Connect** with Keycloak
- **JWT Token Validation** at API Gateway
- **Role-based Access Control** (USER/ADMIN)
- **CORS Configuration** for web clients
- **Secure Service-to-Service** communication

## 🔄 Saga Pattern Implementation

### Order Creation Saga
```
1. Order Created → 2. Payment Processing → 3. Notification Sent
                     ↓ (if failed)
4. Order Cancelled ← 5. Payment Refund ← 6. Cancel Notification
```

### Saga Steps
- **Step 1**: Order Service creates order
- **Step 2**: Payment Service processes payment
- **Step 3**: Notification Service sends confirmation
- **Compensation**: Automatic rollback on any failure

### Event Flow
```
Order Created Event → Payment Processing → Payment Processed Event → Notification Event
                                        ↓
                                   Order Confirmed
```

## 🛡️ Fault Tolerance

### Circuit Breaker Pattern
- **Sliding Window**: 10 requests
- **Failure Threshold**: 50%
- **Open State Duration**: 30 seconds
- **Half-Open Calls**: 3 attempts

### Retry Mechanism
- **Max Attempts**: 3
- **Wait Duration**: 1 second
- **Backoff Multiplier**: 2x

### Bulkhead Pattern
- **Resource Isolation**: Separate thread pools
- **Max Concurrent Calls**: 20 per service

## 🗃️ Database Architecture

### PostgreSQL (User & Product Services)
```sql
-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Products Table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### MongoDB (Order Service)
```javascript
// Orders Collection
{
  _id: ObjectId,
  userId: String,
  items: [{
    productId: String,
    productName: String,
    quantity: Number,
    price: Number,
    totalPrice: Number
  }],
  totalAmount: Number,
  status: String, // PENDING, CONFIRMED, SHIPPED, etc.
  shippingAddress: String,
  paymentMethod: String,
  sagaId: String,
  createdAt: Date,
  updatedAt: Date
}

// Sagas Collection
{
  _id: ObjectId,
  orderId: String,
  userId: String,
  status: String, // STARTED, IN_PROGRESS, COMPLETED, FAILED
  steps: [{
    stepName: String,
    status: String,
    compensationAction: String,
    executedAt: Date,
    errorMessage: String
  }],
  currentStep: String,
  createdAt: Date,
  updatedAt: Date
}
```

### MySQL (Payment Service)
```sql
-- Payments Table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(255) UNIQUE NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    payment_method ENUM('CREDIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CRYPTO', 'WALLET') NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED') NOT NULL,
    transaction_id VARCHAR(255),
    gateway_response TEXT,
    failure_reason TEXT,
    saga_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL
);
```

### Redis (Notification Service)
```
# Notifications Hash
notifications:{id} = {
  id: String,
  userId: String,
  recipient: String,
  subject: String,
  message: String,
  type: String, // EMAIL, SMS, PUSH, IN_APP
  status: String, // PENDING, SENT, FAILED, DELIVERED
  orderId: String,
  sagaId: String,
  createdAt: Date,
  sentAt: Date,
  errorMessage: String,
  retryCount: Number
}
```

## 📡 API Documentation

### Authentication APIs
```bash
# Register User
POST /api/auth/register
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890"
}

# Login User
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Product APIs
```bash
# Get All Products
GET /api/products?page=0&size=10&sortBy=name&sortDir=asc

# Get Product by ID
GET /api/products/{id}

# Search Products
GET /api/products/search?query=iPhone

# Create Product (Admin)
POST /api/products
{
  "name": "iPhone 15",
  "description": "Latest iPhone model",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "brand": "Apple"
}

# Update Stock (Admin)
PUT /api/products/{id}/stock?quantity=100
```

### Order APIs
```bash
# Create Order
POST /api/orders
{
  "items": [{
    "productId": "1",
    "quantity": 2,
    "price": 999.99
  }],
  "shippingAddress": "123 Main St, City, State 12345",
  "paymentMethod": "CREDIT_CARD"
}

# Get User Orders
GET /api/orders?page=0&size=10

# Get Order by ID
GET /api/orders/{id}
```

### Payment APIs
```bash
# Process Payment
POST /api/payments
{
  "orderId": "order-123",
  "amount": 1999.98,
  "paymentMethod": "CREDIT_CARD",
  "creditCard": {
    "cardNumber": "4111111111111111",
    "expiryMonth": "12",
    "expiryYear": "2025",
    "cvv": "123",
    "cardholderName": "John Doe"
  }
}

# Get Payment by Order ID
GET /api/payments/order/{orderId}

# Refund Payment (Admin)
POST /api/payments/{paymentId}/refund
```

### Notification APIs
```bash
# Send Notification
POST /api/notifications
{
  "recipient": "john@example.com",
  "subject": "Order Confirmation",
  "message": "Your order has been confirmed",
  "type": "EMAIL",
  "userId": "user-123",
  "orderId": "order-123"
}

# Get User Notifications
GET /api/notifications/user/{userId}

# Mark as Read
PUT /api/notifications/{notificationId}/read
```

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
POSTGRES_URL=jdbc:postgresql://localhost:5434/ekart
POSTGRES_USER=ekart
POSTGRES_PASSWORD=ekart123

MONGODB_URI=mongodb://ekart:ekart123@localhost:27017/orders?authSource=admin

MYSQL_URL=jdbc:mysql://localhost:3306/payments
MYSQL_USER=ekart
MYSQL_PASSWORD=ekart123

REDIS_HOST=localhost
REDIS_PORT=6379

# Keycloak Configuration
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8090
KEYCLOAK_REALM=ekart
KEYCLOAK_CLIENT_ID=ekart-client
KEYCLOAK_CLIENT_SECRET=your-client-secret

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Email Configuration (for notifications)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 📊 Monitoring & Observability

### Health Checks
```bash
# API Gateway Health
curl http://localhost:8080/actuator/health

# Individual Service Health
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Product Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8084/actuator/health  # Payment Service
curl http://localhost:8085/actuator/health  # Notification Service
```

### Service Discovery
```bash
# Eureka Dashboard
http://localhost:8761

# Registered Services
curl http://localhost:8761/eureka/apps -H "Accept: application/json"
```

### Metrics
```bash
# Gateway Metrics
curl http://localhost:8080/actuator/metrics

# Circuit Breaker Status
curl http://localhost:8080/actuator/circuitbreakers
```

## 🧪 Testing

### Unit Testing
```bash
# Run tests for individual service
cd user-service
mvn test

# Run tests for all services
mvn test
```

### Integration Testing
```bash
# Test all APIs
./test-complete-apis.sh

# Test specific functionality
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password123"}'
```

### Load Testing
```bash
# Use Apache Bench for load testing
ab -n 1000 -c 10 http://localhost:8080/api/products

# Use curl for stress testing
for i in {1..100}; do
  curl -s http://localhost:8080/api/products > /dev/null &
done
```

## 📈 Performance Considerations

### Database Optimization
- **Connection Pooling**: Configured for all databases
- **Indexing**: Proper indexes on frequently queried fields
- **Pagination**: Implemented for large result sets
- **Caching**: Redis for frequently accessed data

### Service Performance
- **Async Processing**: For notifications and heavy operations
- **Circuit Breakers**: Prevent cascading failures
- **Bulkhead Pattern**: Resource isolation
- **Connection Timeouts**: Prevent hanging requests

### Scalability
- **Horizontal Scaling**: Services can be scaled independently
- **Load Balancing**: Built-in with Eureka
- **Database Sharding**: Can be implemented per service
- **Message Queues**: Kafka for async communication

## 🚨 Troubleshooting

### Common Issues

1. **Services not starting**
   ```bash
   # Check port availability
   netstat -tulpn | grep :8080
   
   # Check Java processes
   jps -l
   
   # Check logs
   tail -f logs/user-service.log
   ```

2. **Database connection errors**
   ```bash
   # Check Docker containers
   docker-compose ps
   
   # Check database logs
   docker-compose logs postgres
   docker-compose logs mongodb
   docker-compose logs mysql
   ```

3. **Authentication issues**
   ```bash
   # Check Keycloak status
   curl http://localhost:8090/realms/ekart/.well-known/openid_configuration
   
   # Verify client configuration
   # Login to Keycloak admin: http://localhost:8090/admin
   ```

4. **Service discovery problems**
   ```bash
   # Check Eureka dashboard
   open http://localhost:8761
   
   # Verify service registration
   curl http://localhost:8761/eureka/apps -H "Accept: application/json"
   ```

### Debug Commands
```bash
# Check all running services
ps aux | grep java

# Monitor system resources
top
df -h

# Check network connectivity
telnet localhost 8080
telnet localhost 8761

# View Docker logs
docker-compose logs -f keycloak
docker-compose logs -f kafka
```

## 🔮 Future Enhancements

### Planned Features
- [ ] **API Versioning** - Support multiple API versions
- [ ] **Rate Limiting** - Per-user and per-endpoint limits
- [ ] **Distributed Tracing** - Zipkin/Jaeger integration
- [ ] **Metrics Collection** - Prometheus/Grafana
- [ ] **Centralized Logging** - ELK Stack
- [ ] **API Documentation** - Swagger/OpenAPI
- [ ] **GraphQL Gateway** - Alternative to REST
- [ ] **WebSocket Support** - Real-time notifications
- [ ] **Machine Learning** - Product recommendations
- [ ] **Mobile App** - React Native application

### Infrastructure Improvements
- [ ] **Kubernetes Deployment** - Container orchestration
- [ ] **Helm Charts** - Kubernetes package management
- [ ] **CI/CD Pipeline** - GitHub Actions/Jenkins
- [ ] **Blue-Green Deployment** - Zero-downtime deployments
- [ ] **Disaster Recovery** - Multi-region setup
- [ ] **Security Scanning** - OWASP ZAP integration
- [ ] **Performance Monitoring** - APM tools
- [ ] **Automated Testing** - E2E test automation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- 📧 Email: support@ekart.com
- 💬 Discord: [E-Kart Community](https://discord.gg/ekart)
- 📖 Documentation: [Wiki](https://github.com/ekart/microservices/wiki)
- 🐛 Issues: [GitHub Issues](https://github.com/ekart/microservices/issues)

---

**Built with ❤️ by the E-Kart Team**

*Happy Coding! 🚀*

## 🔗 Internal Service Communication

### 1. Service Discovery & Registration

All microservices register themselves with **Eureka Server** on startup:

```yaml
# Common configuration in all services
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

**Registration Process:**
1. Service starts up
2. Connects to Eureka Server (localhost:8761)
3. Registers with service name and instance details
4. Sends heartbeat every 30 seconds
5. Other services can discover it via service name

### 2. API Gateway Routing

The **API Gateway** acts as the single entry point and routes requests to appropriate services:

```yaml
# API Gateway routing configuration
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service          # Load balanced URI
          predicates:
            - Path=/api/users/**
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
        - id: payment-service
          uri: lb://payment-service
          predicates:
            - Path=/api/payments/**
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/notifications/**
```

**Routing Flow:**
```
Client Request → API Gateway → Service Discovery → Target Service
     ↓
1. Client sends request to http://localhost:8080/api/products/1
2. API Gateway matches /api/products/** pattern
3. Gateway queries Eureka for "product-service" instances
4. Gateway forwards request to available product-service instance
5. Response flows back through gateway to client
```

### 3. Synchronous Communication (Feign Client)

Services communicate synchronously using **OpenFeign** clients:

```java
// Order Service calling Product Service
@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @GetMapping("/api/products/{id}")
    ProductDto getProductById(@PathVariable Long id);
    
    @PutMapping("/api/products/{id}/stock")
    ApiResponse updateStock(@PathVariable Long id, @RequestParam Integer quantity);
}

// Usage in Order Service
@Service
public class OrderService {
    
    private final ProductServiceClient productClient;
    
    public Order createOrder(CreateOrderRequest request) {
        // 1. Validate products exist and have stock
        for (OrderItem item : request.getItems()) {
            ProductDto product = productClient.getProductById(item.getProductId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException();
            }
        }
        
        // 2. Create order
        Order order = new Order();
        // ... set order details
        
        // 3. Reserve stock (synchronous call)
        for (OrderItem item : request.getItems()) {
            productClient.updateStock(item.getProductId(), 
                                    -item.getQuantity()); // Reserve stock
        }
        
        return orderRepository.save(order);
    }
}
```

**Feign Client Features:**
- **Service Discovery Integration**: Uses service names instead of hardcoded URLs
- **Load Balancing**: Automatically distributes requests across service instances
- **Circuit Breaker**: Prevents cascading failures
- **Retry Logic**: Automatically retries failed requests

### 4. Asynchronous Communication (Kafka)

Services communicate asynchronously using **Kafka** for event-driven architecture:

```java
// Order Service - Publishing Events
@Service
public class OrderSagaOrchestrator {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void startOrderSaga(Order order) {
        // Create order created event
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(order.getId());
        event.setUserId(order.getUserId());
        event.setTotalAmount(order.getTotalAmount());
        event.setSagaId(sagaId);
        
        // Publish to Kafka topic
        kafkaTemplate.send("order-created-topic", event);
    }
}

// Payment Service - Consuming Events
@Service
public class PaymentService {
    
    @KafkaListener(topics = "order-created-topic")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Process payment for the order
        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        paymentRequest.setOrderId(event.getOrderId());
        paymentRequest.setAmount(event.getTotalAmount());
        
        // Process payment and publish result
        PaymentResult result = processPayment(paymentRequest);
        
        // Publish payment processed event
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent();
        paymentEvent.setSagaId(event.getSagaId());
        paymentEvent.setOrderId(event.getOrderId());
        paymentEvent.setSuccess(result.isSuccess());
        
        kafkaTemplate.send("payment-processed-topic", paymentEvent);
    }
}
```

**Kafka Topics & Event Flow:**
```
order-created-topic:
  Publisher: Order Service
  Consumers: Payment Service

payment-processed-topic:
  Publisher: Payment Service
  Consumers: Order Service (Saga), Notification Service

notification-topic:
  Publisher: Order Service, Payment Service
  Consumers: Notification Service
```

### 5. Database Connectivity

Each service has its own dedicated database:

```yaml
# User Service - PostgreSQL
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/ekart
    username: ekart
    password: ekart123
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

# Order Service - MongoDB
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: orders
      username: ekart
      password: ekart123
      authentication-database: admin

# Payment Service - MySQL
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/payments
    username: ekart
    password: ekart123
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect

# Notification Service - Redis
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

### 6. Authentication & Authorization Flow

**JWT Token Validation Flow:**
```
1. Client → API Gateway (with JWT token)
2. API Gateway → Keycloak (token validation)
3. API Gateway → Target Service (with validated user context)
4. Target Service → Database (with user authorization)
```

```java
// Security Configuration in each service
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtDecoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/users/**").hasRole("USER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 7. Circuit Breaker Pattern

**Hystrix/Resilience4j** prevents cascading failures:

```java
// Order Service calling Product Service with Circuit Breaker
@Service
public class OrderService {
    
    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductFallback")
    @Retry(name = "product-service")
    public ProductDto getProduct(Long productId) {
        return productServiceClient.getProductById(productId);
    }
    
    // Fallback method
    public ProductDto getProductFallback(Long productId, Exception ex) {
        log.warn("Product service unavailable, using fallback for product: {}", productId);
        
        ProductDto fallback = new ProductDto();
        fallback.setId(productId);
        fallback.setName("Product Unavailable");
        fallback.setPrice(BigDecimal.ZERO);
        return fallback;
    }
}
```

**Circuit Breaker Configuration:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      product-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 3
  retry:
    instances:
      product-service:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
```

### 8. Saga Pattern Implementation

**Distributed Transaction Management:**

```java
// Order Service - Saga Orchestrator
@Service
public class OrderSagaOrchestrator {
    
    public void startOrderSaga(Order order) {
        // Step 1: Create saga
        Saga saga = createSaga(order);
        
        // Step 2: Start payment processing
        publishOrderCreatedEvent(order, saga.getId());
        
        // Step 3: Wait for payment response
        // Step 4: Send notification
        // Step 5: Complete saga
    }
    
    @KafkaListener(topics = "payment-processed-topic")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        Saga saga = sagaRepository.findById(event.getSagaId());
        
        if (event.isSuccess()) {
            // Continue to next step
            publishNotificationEvent(event);
            updateSagaStep(saga, "NOTIFICATION_SENT");
        } else {
            // Start compensation
            startCompensation(saga);
        }
    }
    
    private void startCompensation(Saga saga) {
        // Reverse all completed steps
        // 1. Cancel order
        // 2. Refund payment
        // 3. Send cancellation notification
    }
}
```

### 9. Configuration Management

**Centralized Configuration with Config Server:**

```yaml
# Config Server serves configurations for all services
# Configurations stored in Git repository or local files

# Example: application-dev.yml for all services
spring:
  profiles:
    active: dev
    
# Service-specific configurations
# user-service-dev.yml
server:
  port: 8081
database:
  url: jdbc:postgresql://localhost:5434/ekart_dev
  
# order-service-dev.yml  
server:
  port: 8083
mongodb:
  uri: mongodb://localhost:27017/orders_dev
```

### 10. Service Startup Sequence

**Proper Service Initialization Order:**

```bash
# 1. Infrastructure Services
docker-compose up -d  # Databases, Kafka, Keycloak

# 2. Core Services
mvn spring-boot:run   # Config Server (Port 8888)
mvn spring-boot:run   # Eureka Server (Port 8761)

# 3. Gateway Service
mvn spring-boot:run   # API Gateway (Port 8080)

# 4. Business Services (can start in parallel)
mvn spring-boot:run   # User Service (Port 8081)
mvn spring-boot:run   # Product Service (Port 8082)
mvn spring-boot:run   # Order Service (Port 8083)
mvn spring-boot:run   # Payment Service (Port 8084)
mvn spring-boot:run   # Notification Service (Port 8085)
```

### 11. Inter-Service Communication Patterns

**Synchronous Patterns:**
- **Request-Response**: API Gateway → Services
- **Service-to-Service**: Order → Product (stock check)
- **Chain Calls**: Gateway → User → Database

**Asynchronous Patterns:**
- **Event Publishing**: Order Service → Kafka
- **Event Consumption**: Payment Service ← Kafka
- **Saga Orchestration**: Order Service coordinates workflow

**Communication Matrix:**
```
┌─────────────┬─────────┬─────────┬──────────┬──────────┬──────────┐
│   Service   │  User   │Product  │  Order   │ Payment  │  Notify  │
├─────────────┼─────────┼─────────┼──────────┼──────────┼──────────┤
│ User        │    -    │    -    │    -     │    -     │    -     │
│ Product     │    -    │    -    │    -     │    -     │    -     │
│ Order       │  Feign  │  Feign  │    -     │  Kafka   │  Kafka   │
│ Payment     │    -    │    -    │  Kafka   │    -     │  Kafka   │
│ Notification│    -    │    -    │  Kafka   │  Kafka   │    -     │
└─────────────┴─────────┴─────────┴──────────┴──────────┴──────────┘
```

This architecture ensures **loose coupling**, **high availability**, and **scalable communication** between all microservices
