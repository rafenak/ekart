# 🚀 E-Kart Microservices - Quick Start Guide

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- curl and jq (for testing)

## 🏃‍♂️ Quick Start

### 1. Start Infrastructure
```bash
# Start databases and Keycloak
docker-compose up -d

# Wait for services to be ready (about 2 minutes)
```

### 2. Configure Keycloak
```bash
# Setup Keycloak realm, client, and users
./setup-keycloak.sh
```

### 3. Start Microservices
```bash
# Start all microservices
./start-services.sh
```

### 4. Test the APIs
```bash
# Run API tests
./test-apis.sh
```

### 5. Stop Everything
```bash
# Stop all services
./stop-services.sh
```

## 📡 Service Endpoints

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| API Gateway | 8080 | http://localhost:8080 | Main entry point |
| Eureka Server | 8761 | http://localhost:8761 | Service discovery |
| Config Server | 8888 | http://localhost:8888 | Configuration |
| User Service | 8081 | http://localhost:8081 | User management |
| Product Service | 8082 | http://localhost:8082 | Product catalog |
| Order Service | 8083 | http://localhost:8083 | Order management |
| Keycloak | 8090 | http://localhost:8090 | Authentication |

## 🔐 Authentication

### Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }'
```

### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

## 📦 API Examples

### Get Products (Public):
```bash
curl http://localhost:8080/api/products
```

### Create Product (Admin only):
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "description": "Latest iPhone model",
    "price": 999.99,
    "stockQuantity": 50,
    "category": "Electronics",
    "brand": "Apple"
  }'
```

### Create Order (Authenticated):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{
      "productId": "1",
      "quantity": 2,
      "price": 999.99
    }],
    "shippingAddress": "123 Main St, City, State 12345",
    "paymentMethod": "CREDIT_CARD"
  }'
```

## 🛠️ Development

### Building Individual Services:
```bash
cd user-service
mvn clean package
mvn spring-boot:run
```

### Viewing Logs:
```bash
# View logs for a specific service
tail -f logs/user-service.log

# View all Docker logs
docker-compose logs -f
```

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **Actuator Health**: http://localhost:8080/actuator/health
- **Keycloak Console**: http://localhost:8090/admin (admin/admin123)

## 🔧 Configuration

Services are configured via `application.yml` files in each service directory. Key configurations:

- Database connections
- Keycloak settings
- Service discovery
- Circuit breaker settings
- Kafka configuration

## 🎯 Features Implemented

✅ **Microservices Architecture**
- Service discovery with Eureka
- API Gateway with Spring Cloud Gateway
- Configuration management

✅ **Security**
- OAuth2/OpenID Connect with Keycloak
- JWT token validation
- Role-based access control

✅ **Fault Tolerance**
- Circuit breaker pattern
- Retry mechanism
- Bulkhead isolation

✅ **Saga Pattern**
- Distributed transaction management
- Compensation handling
- Event-driven architecture

✅ **Multiple Databases**
- PostgreSQL for users and products
- MongoDB for orders and sagas
- MySQL for payments
- Redis for caching

✅ **Event Streaming**
- Kafka for async communication
- Event sourcing patterns
- Saga orchestration

## 🚨 Troubleshooting

### Common Issues:

1. **Services not starting**: Check if ports are available
2. **Database connection errors**: Ensure Docker containers are running
3. **Keycloak authentication issues**: Verify client configuration
4. **Service discovery problems**: Check Eureka server status

### Debug Commands:
```bash
# Check service status
docker-compose ps

# View specific service logs
docker-compose logs keycloak

# Check Java processes
jps -l

# Test connectivity
curl -I http://localhost:8761
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

---

**Happy Coding! 🎉**
