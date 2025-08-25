# 🛒 E-Kart Microservices Platform

A production-ready e-commerce microservices architecture built with Spring Boot, featuring comprehensive functionality from authentication to order management.

## 🚀 Features

### 🏗️ **Architecture & Infrastructure**
- **Microservices Architecture** with Spring Boot 3.2.0
- **API Gateway** (Spring Cloud Gateway) with routing and CORS
- **Service Discovery** (Eureka Server) for dynamic service registration
- **Centralized Configuration** (Config Server) with local/remote fallback
- **Circuit Breaker Pattern** (Resilience4j) for fault tolerance
- **Saga Pattern** for distributed transaction management

### 🔐 **Authentication & Security**
- **OAuth2/OpenID Connect** integration with Keycloak
- **JWT Token** validation and management
- **Role-based Access Control** (USER/ADMIN)
- **Secure API endpoints** with proper authorization

### 🛍️ **E-commerce Core Features**
- **User Management** - Registration, login, profile management
- **Product Catalog** - CRUD operations, search, categories, reviews
- **Shopping Cart** - Add/remove items, quantity management
- **Order Management** - Create orders, track status, order history
- **Payment Processing** - Secure payment handling with Saga pattern
- **Notifications** - Email/SMS notifications for order updates

### 📊 **Advanced Features**
- **Product Reviews & Ratings** - User-generated content
- **Featured Products** - Promotional product highlighting  
- **Inventory Management** - Stock tracking and low-stock alerts
- **Order Analytics** - Order summaries and reporting
- **Multi-database Support** - PostgreSQL, MongoDB, MySQL, Redis

### 🔧 **DevOps & Monitoring**
- **Docker Containerization** with docker-compose
- **Health Checks** and metrics endpoints
- **Centralized Logging** with structured log patterns
- **Circuit Breaker Monitoring** and automatic recovery

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  Config Server  │    │ Eureka Server   │
│   Port: 8080    │    │   Port: 8888    │    │   Port: 8761    │
│  ┌─────────────┐│    │  ┌─────────────┐│    │  ┌─────────────┐│
│  │JWT Auth     ││    │  │Local Config ││    │  │Service Disc.││
│  │Rate Limiting││    │  │Git Fallback ││    │  │Health Check ││
│  │CORS Handler ││    │  │Hot Reload   ││    │  │Load Balance ││
│  └─────────────┘│    │  └─────────────┘│    │  └─────────────┘│
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
    ┌────────────────────────────┼────────────────────────────┐
    │                            │                            │
┌───▼───┐  ┌─────────┐  ┌───────▼──┐  ┌─────────────┐  ┌─────────────┐
│ User  │  │Product  │  │  Order   │  │  Payment    │  │Notification │
│Service│  │Service  │  │ Service  │  │  Service    │  │  Service    │
│:8081  │  │ :8082   │  │  :8083   │  │   :8084     │  │   :8085     │
└───┬───┘  └────┬────┘  └─────┬────┘  └──────┬──────┘  └──────┬──────┘
    │           │             │              │                │
┌───▼───┐  ┌───▼────┐   ┌────▼─────┐   ┌───▼─────┐      ┌──▼───┐
│Keycloak│ │PostgreSQL│  │ MongoDB  │   │ MySQL   │      │Redis │
│:8090   │ │ :5434    │  │ :27017   │   │ :3306   │      │:6379 │
└────────┘ └──────────┘  └──────────┘   └─────────┘      └──────┘
```

## 🎯 Services Overview

### 1. **API Gateway** (Port: 8080)
**Main Entry Point for All Requests**
- **Routing:** Intelligent request routing to appropriate microservices
- **Authentication:** JWT token validation and user context propagation
- **Security:** CORS handling, rate limiting, and request/response logging
- **Fault Tolerance:** Circuit breaker integration for service failures
- **Load Balancing:** Automatic load balancing across service instances

### 2. **Config Server** (Port: 8888)
**Centralized Configuration Management**
- **Local Configuration:** Embedded configuration files for offline operation
- **Remote Git Support:** Fallback to Git repositories for production
- **Hot Reload:** Dynamic configuration updates without service restart
- **Environment Profiles:** Support for dev, staging, production configurations
- **Security:** Basic authentication for configuration access

### 3. **Eureka Server** (Port: 8761)
**Service Discovery & Registration**
- **Service Registry:** Automatic service registration and discovery
- **Health Monitoring:** Continuous health checks and service status tracking
- **Load Balancing:** Client-side load balancing with service instances
- **Fault Detection:** Automatic service removal on failures
- **Dashboard:** Web UI for monitoring registered services

### 4. **User Service** (Port: 8081)
**Authentication & User Management**
- **Authentication:** OAuth2/OpenID Connect integration with Keycloak
- **User Registration:** Complete user onboarding with validation
- **Profile Management:** Update user information, change passwords
- **Role Management:** USER/ADMIN role-based access control
- **Database:** PostgreSQL for user data persistence

### 5. **Product Service** (Port: 8082)
**Product Catalog & Inventory**
- **Product CRUD:** Complete product lifecycle management
- **Search & Filtering:** Advanced product search with categories
- **Reviews & Ratings:** User-generated product reviews and ratings
- **Inventory Management:** Stock tracking and low-stock alerts
- **Featured Products:** Promotional product highlighting
- **Database:** PostgreSQL for product data and reviews

### 6. **Order Service** (Port: 8083)
**Order Processing & Management**
- **Order Creation:** Complete order workflow with validation
- **Order Tracking:** Real-time order status updates
- **Order History:** User order history and details
- **Saga Orchestration:** Distributed transaction coordination
- **Order Analytics:** Order summaries and reporting
- **Database:** MongoDB for flexible order document storage

### 7. **Payment Service** (Port: 8084)
**Payment Processing**
- **Payment Processing:** Secure payment transaction handling
- **Payment Methods:** Support for multiple payment options
- **Transaction History:** Complete payment audit trail
- **Refund Management:** Payment refund capabilities
- **Saga Participation:** Integration with order saga pattern
- **Database:** MySQL for financial transaction data

### 8. **Notification Service** (Port: 8085)
**Communication & Alerts**
- **Email Notifications:** Order confirmations, status updates
- **SMS Support:** Text message notifications (configurable)
- **Template Management:** Dynamic email template system
- **Notification History:** Track sent notifications
- **Async Processing:** Background notification processing
- **Database:** Redis for notification queuing and caching

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+** for building
- **Docker & Docker Compose** for infrastructure
- **Node.js 18+** for frontend development

### 1. **Clone Repository**
```bash
git clone <repository-url>
cd ekart-service
```

### 2. **Start Infrastructure Services**
```bash
# Start databases and Keycloak
docker-compose up -d

# Setup Keycloak (automated setup with proper client configuration)
./setup-keycloak.sh
```

**📖 For detailed Keycloak setup instructions, see [KEYCLOAK_SETUP.md](./KEYCLOAK_SETUP.md)**

### 3. **Build All Services**
```bash
# Build all microservices
mvn clean install

# Or build individual services
cd user-service && mvn clean install
```

### 4. **Start Backend Services**
```bash
# Start all services with script
./start-services.sh

# Or start manually in order:
java -jar config-server/target/config-server-1.0.0.jar
java -jar eureka-server/target/eureka-server-1.0.0.jar
java -jar api-gateway/target/api-gateway-1.0.0.jar
java -jar user-service/target/user-service-1.0.0.jar
java -jar product-service/target/product-service-1.0.0.jar
java -jar order-service/target/order-service-1.0.0.jar
java -jar payment-service/target/payment-service-1.0.0.jar
java -jar notification-service/target/notification-service-1.0.0.jar
```

### 5. **Start Frontend**
```bash
cd ekart-frontend
npm install
npm run dev
```

### 6. **Access Applications**
- **Frontend:** http://localhost:3000
- **API Gateway:** http://localhost:8080
- **Eureka Dashboard:** http://localhost:8761
- **Config Server:** http://localhost:8888
- **Keycloak Admin:** http://localhost:8090

## 🧪 Testing & Validation

### **Test API Endpoints**
```bash
# Test complete API functionality
./test-complete-apis.sh

# Test basic endpoints
./test-apis.sh
```

### **Health Checks**
```bash
# Check all services health
curl http://localhost:8080/actuator/health
curl http://localhost:8761/actuator/health
curl http://localhost:8888/actuator/health
```

## 📚 API Documentation

### **Authentication APIs**
```bash
# Register User
POST /api/auth/register
Content-Type: application/json
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "+1234567890"
}

# Login User  
POST /api/auth/login
Content-Type: application/json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### **User Management APIs**
```bash
# Get User Profile
GET /api/users/profile?email=john@example.com
Authorization: Bearer {jwt_token}

# Update User Profile
PUT /api/users/profile?email=john@example.com
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "+1234567890",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}

# Change Password
POST /api/users/change-password?email=john@example.com
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "currentPassword": "oldPassword",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

### **Product Management APIs**
```bash
# Get All Products (with pagination)
GET /api/products?page=0&size=10&sortBy=name&sortDir=asc

# Get Product by ID
GET /api/products/{productId}

# Search Products
GET /api/products/search?query=smartphone&page=0&size=10

# Get Products by Category
GET /api/products/category/electronics?page=0&size=10

# Get Featured Products
GET /api/products/featured?page=0&size=5

# Get Top Rated Products  
GET /api/products/top-rated?page=0&size=5

# Create Product (Admin only)
POST /api/products
Authorization: Bearer {admin_jwt_token}
Content-Type: application/json
{
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with advanced features",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "brand": "Apple",
  "sku": "IPH15PRO001",
  "weight": 0.2,
  "dimensions": "6.1 x 2.9 x 0.3 inches",
  "featured": true
}

# Update Stock (Admin only)
PUT /api/products/{productId}/stock?quantity=100
Authorization: Bearer {admin_jwt_token}
```

### **Product Review APIs**
```bash
# Create Product Review
POST /api/products/{productId}/reviews?userId=user123&userName=John
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "rating": 5,
  "comment": "Excellent product, highly recommended!",
  "title": "Outstanding Quality"
}

# Get Product Reviews
GET /api/products/{productId}/reviews?page=0&size=10

# Get User's Review for Product
GET /api/products/{productId}/reviews/user?userId=user123

# Update Review
PUT /api/reviews/{reviewId}?userId=user123
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "rating": 4,
  "comment": "Updated review comment",
  "title": "Good Product"
}

# Delete Review
DELETE /api/reviews/{reviewId}?userId=user123
Authorization: Bearer {jwt_token}
```

### **Order Management APIs**
```bash
# Create Order
POST /api/orders
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "items": [
    {
      "productId": "1",
      "quantity": 2,
      "price": 999.99
    }
  ],
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "paymentMethod": "CREDIT_CARD"
}

# Get User Orders
GET /api/orders?page=0&size=10
Authorization: Bearer {jwt_token}

# Get Order by ID
GET /api/orders/{orderId}
Authorization: Bearer {jwt_token}

# Get Order Status
GET /api/orders/{orderId}/status
Authorization: Bearer {jwt_token}

# Cancel Order
PUT /api/orders/{orderId}/cancel
Authorization: Bearer {jwt_token}

# Get Order Summary
GET /api/orders/summary
Authorization: Bearer {jwt_token}
```

### **Payment APIs**
```bash
# Process Payment
POST /api/payments
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "orderId": "order123",
  "amount": 1999.98,
  "paymentMethod": "CREDIT_CARD",
  "cardDetails": {
    "cardNumber": "****-****-****-1234",
    "expiryMonth": "12",
    "expiryYear": "2025",
    "cvv": "***"
  }
}

# Get Payment by Order ID
GET /api/payments/order/{orderId}
Authorization: Bearer {jwt_token}

# Get User Payments
GET /api/payments/user/{userId}?page=0&size=10
Authorization: Bearer {jwt_token}

# Refund Payment (Admin only)
POST /api/payments/{paymentId}/refund
Authorization: Bearer {admin_jwt_token}
```

### **Notification APIs**
```bash
# Send Notification
POST /api/notifications
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "userId": "user123",
  "type": "ORDER_CONFIRMATION",
  "title": "Order Confirmed",
  "message": "Your order has been confirmed",
  "email": "john@example.com"
}

# Get User Notifications
GET /api/notifications/user/{userId}
Authorization: Bearer {jwt_token}

# Mark Notification as Read
PUT /api/notifications/{notificationId}/read
Authorization: Bearer {jwt_token}
```

## 🏗️ Technical Architecture

### **Design Patterns Implemented**
- **Microservices Architecture:** Loosely coupled, independently deployable services
- **API Gateway Pattern:** Single entry point for all client requests
- **Service Discovery:** Dynamic service registration and discovery
- **Circuit Breaker Pattern:** Fault tolerance and graceful degradation
- **Saga Pattern:** Distributed transaction management
- **CQRS:** Command Query Responsibility Segregation for complex operations
- **Event Sourcing:** Event-driven architecture for order processing

### **Database Strategy**
- **PostgreSQL:** User management, product catalog (ACID compliance)
- **MongoDB:** Order management (flexible document structure)
- **MySQL:** Payment transactions (financial data integrity)
- **Redis:** Caching, session management, notifications

### **Security Implementation**
- **OAuth2/OpenID Connect:** Industry-standard authentication
- **JWT Tokens:** Stateless authentication with proper validation
- **Role-based Access Control:** Granular permission management
- **API Security:** Proper HTTPS, CORS, and rate limiting
- **Data Encryption:** Sensitive data protection

### **Fault Tolerance & Resilience**
- **Circuit Breaker:** Automatic failure detection and recovery
- **Retry Logic:** Intelligent retry mechanisms with exponential backoff
- **Bulkhead Pattern:** Service isolation to prevent cascade failures
- **Health Checks:** Comprehensive service health monitoring
- **Graceful Degradation:** Fallback mechanisms for service failures

## 🔧 Configuration Management

### **Local Development Configuration**
Configuration files are embedded in the config-server for offline development:
- `config-server/src/main/resources/config/`
- `config/` (external directory for overrides)

### **Configuration Hierarchy**
1. **Local embedded configs** (highest priority)
2. **External config directory** 
3. **Remote Git repository** (fallback)

### **Environment-Specific Configs**
- **Development:** Local database connections, debug logging
- **Staging:** Staging database, moderate logging
- **Production:** Production database, optimized logging

## 🚦 Monitoring & Operations

### **Health Monitoring**
All services expose health endpoints:
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Performance metrics

### **Logging Strategy**
- **Structured Logging:** JSON format for log aggregation
- **Correlation IDs:** Request tracing across services
- **Log Levels:** Configurable logging levels per service
- **Centralized Logs:** All services log to stdout for container orchestration

### **Circuit Breaker Monitoring**
- **Resilience4j Dashboard:** Circuit breaker state monitoring
- **Metrics Collection:** Success/failure rates, response times
- **Automatic Recovery:** Self-healing mechanisms

## 📦 Deployment Options

### **Local Development**
```bash
# Using provided scripts
./start-services.sh

# Manual startup
java -jar {service}/target/{service}-1.0.0.jar
```

### **Docker Deployment**
```bash
# Infrastructure only
docker-compose up -d

# With application services (when Dockerfiles are added)
docker-compose -f docker-compose.full.yml up -d
```

### **Production Deployment**
- **Kubernetes:** Helm charts for orchestration
- **AWS ECS/EKS:** Container-based deployment
- **Spring Boot JAR:** Traditional JAR deployment

## 🧪 Testing Strategy

### **Testing Pyramid**
- **Unit Tests:** Individual component testing
- **Integration Tests:** Service interaction testing
- **Contract Tests:** API contract validation
- **End-to-End Tests:** Complete workflow testing

### **API Testing**
```bash
# Automated API testing
./test-complete-apis.sh

# Manual API testing
./test-apis.sh

# Load testing with provided scripts
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

## 🤝 Contributing

### **Development Workflow**
1. **Fork Repository:** Create your feature branch
2. **Local Development:** Test changes locally
3. **API Testing:** Validate with provided test scripts
4. **Documentation:** Update relevant documentation
5. **Pull Request:** Submit for review

### **Code Standards**
- **Java:** Follow Spring Boot best practices
- **Database:** Use proper indexes and constraints
- **API Design:** RESTful conventions with proper HTTP status codes
- **Security:** Always validate input and implement proper authorization

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support & Troubleshooting

### **Common Issues**
1. **Port Conflicts:** Ensure ports 8080-8090, 5434, 27017, 3306, 6379 are available
2. **Database Connection:** Verify database services are running
3. **Keycloak Setup:** Run setup script after infrastructure startup
4. **Service Discovery:** Check Eureka dashboard for service registration

### **Getting Help**
- **Documentation:** Check COMPLETE_DOCUMENTATION.md for detailed setup
- **API Testing:** Use provided test scripts for validation
- **Logs:** Check service logs for detailed error information

---

**🚀 Happy Coding! Build amazing e-commerce experiences with E-Kart Microservices Platform!**

### 7. Payment Service (Port: 8084)
- Payment processing with Saga pattern
- MySQL database
- Transaction rollback support

### 8. Notification Service (Port: 8085)
- Email/SMS notifications
- Redis for caching
- Async message processing

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Running the Application

1. **Start Infrastructure Services:**
   ```bash
   docker-compose up -d
   ```

2. **Start Services in Order:**
   ```bash
   # 1. Config Server
   cd config-server && mvn spring-boot:run

   # 2. Eureka Server
   cd eureka-server && mvn spring-boot:run

   # 3. Start all microservices
   cd api-gateway && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

## API Endpoints

### Authentication
- POST `/api/auth/login` - User login
- POST `/api/auth/register` - User registration

### Products
- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get product by ID
- POST `/api/products` - Create product (Admin only)

### Orders
- GET `/api/orders` - Get user orders
- POST `/api/orders` - Create new order
- GET `/api/orders/{id}` - Get order by ID

### Payments
- POST `/api/payments` - Process payment
- GET `/api/payments/{orderId}` - Get payment status

## Saga Pattern Implementation

The system uses choreography-based saga pattern for distributed transactions:

1. **Order Creation Saga:**
   - Order Service creates order
   - Payment Service processes payment
   - Notification Service sends confirmation
   - If any step fails, compensating transactions are triggered

## Fault Tolerance

- **Circuit Breaker:** Prevents cascading failures
- **Retry:** Automatic retry with exponential backoff
- **Bulkhead:** Isolates critical resources
- **Timeout:** Prevents hanging requests

## Security

- **Keycloak Integration:** OAuth2/OpenID Connect
- **JWT Token Validation:** In API Gateway
- **Role-based Access Control:** Admin/User roles
- **CORS Configuration:** Cross-origin support

## Monitoring & Observability

- **Actuator Endpoints:** Health checks and metrics
- **Distributed Tracing:** Request correlation
- **Centralized Logging:** JSON structured logs
