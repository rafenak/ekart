#!/bin/bash

# Comprehensive E-Kart API Test Script

API_BASE="http://localhost:8080"
echo "🧪 Testing E-Kart Complete Microservices APIs..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    if [ "$status" = "SUCCESS" ]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [ "$status" = "ERROR" ]; then
        echo -e "${RED}❌ $message${NC}"
    elif [ "$status" = "INFO" ]; then
        echo -e "${BLUE}ℹ️  $message${NC}"
    elif [ "$status" = "WARNING" ]; then
        echo -e "${YELLOW}⚠️  $message${NC}"
    fi
}

# Test service health
print_status "INFO" "Checking API Gateway health..."
HEALTH_RESPONSE=$(curl -s "$API_BASE/actuator/health")
if echo "$HEALTH_RESPONSE" | grep -q '"status":"UP"'; then
    print_status "SUCCESS" "API Gateway is healthy"
else
    print_status "ERROR" "API Gateway is not healthy"
    exit 1
fi

# Test user registration
print_status "INFO" "Testing user registration..."
REGISTER_RESPONSE=$(curl -s -X POST "$API_BASE/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }')

if echo "$REGISTER_RESPONSE" | grep -q '"success":true'; then
    print_status "SUCCESS" "User registration successful"
else
    print_status "WARNING" "User might already exist or registration failed"
fi

# Test user login
print_status "INFO" "Testing user login..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }')

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken // empty')

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    print_status "SUCCESS" "Login successful, token obtained"
    
    # Test Products API
    print_status "INFO" "Testing product retrieval..."
    PRODUCTS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/products")
    if echo "$PRODUCTS_RESPONSE" | grep -q '"success":true'; then
        print_status "SUCCESS" "Products retrieved successfully"
    else
        print_status "ERROR" "Failed to retrieve products"
    fi
    
    # Test creating a product (Admin required)
    print_status "INFO" "Testing product creation (may fail due to permissions)..."
    CREATE_PRODUCT_RESPONSE=$(curl -s -X POST "$API_BASE/api/products" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "Test Product",
        "description": "A test product for API testing",
        "price": 99.99,
        "stockQuantity": 10,
        "category": "Electronics",
        "brand": "TestBrand"
      }')
    
    if echo "$CREATE_PRODUCT_RESPONSE" | grep -q '"success":true'; then
        print_status "SUCCESS" "Product created successfully"
        PRODUCT_ID=$(echo "$CREATE_PRODUCT_RESPONSE" | jq -r '.data.id // empty')
    else
        print_status "WARNING" "Product creation failed (normal for non-admin users)"
        PRODUCT_ID="1" # Use a default product ID for testing
    fi
    
    # Test Order Creation
    print_status "INFO" "Testing order creation..."
    CREATE_ORDER_RESPONSE=$(curl -s -X POST "$API_BASE/api/orders" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "items": [{
          "productId": "'$PRODUCT_ID'",
          "quantity": 2,
          "price": 99.99
        }],
        "shippingAddress": "123 Test Street, Test City, TS 12345",
        "paymentMethod": "CREDIT_CARD"
      }')
    
    if echo "$CREATE_ORDER_RESPONSE" | grep -q '"success":true'; then
        print_status "SUCCESS" "Order created successfully"
        ORDER_ID=$(echo "$CREATE_ORDER_RESPONSE" | jq -r '.data.id // empty')
        
        # Test Payment Processing
        print_status "INFO" "Testing payment processing..."
        sleep 3 # Wait for saga to process
        
        PAYMENT_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/payments/order/$ORDER_ID")
        if echo "$PAYMENT_RESPONSE" | grep -q '"success":true'; then
            print_status "SUCCESS" "Payment processed successfully"
        else
            print_status "WARNING" "Payment processing may still be in progress"
        fi
        
        # Test Order Retrieval
        print_status "INFO" "Testing order retrieval..."
        ORDER_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/orders/$ORDER_ID")
        if echo "$ORDER_RESPONSE" | grep -q '"success":true'; then
            print_status "SUCCESS" "Order retrieved successfully"
        else
            print_status "ERROR" "Failed to retrieve order"
        fi
        
    else
        print_status "ERROR" "Order creation failed"
    fi
    
    # Test User Profile
    print_status "INFO" "Testing user profile retrieval..."
    PROFILE_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/users/profile?email=john.doe@example.com")
    if echo "$PROFILE_RESPONSE" | grep -q '"success":true'; then
        print_status "SUCCESS" "User profile retrieved successfully"
    else
        print_status "ERROR" "Failed to retrieve user profile"
    fi
    
    # Test Notifications
    print_status "INFO" "Testing notification creation..."
    NOTIFICATION_RESPONSE=$(curl -s -X POST "$API_BASE/api/notifications" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "recipient": "john.doe@example.com",
        "subject": "Test Notification",
        "message": "This is a test notification from API test",
        "type": "EMAIL",
        "userId": "test-user-id"
      }')
    
    if echo "$NOTIFICATION_RESPONSE" | grep -q '"success":true'; then
        print_status "SUCCESS" "Notification sent successfully"
    else
        print_status "WARNING" "Notification sending may have failed"
    fi
    
else
    print_status "ERROR" "Login failed or token not found"
    print_status "INFO" "Response: $LOGIN_RESPONSE"
fi

# Test service discovery
print_status "INFO" "Checking Eureka service registry..."
EUREKA_RESPONSE=$(curl -s "http://localhost:8761/eureka/apps" -H "Accept: application/json")
if echo "$EUREKA_RESPONSE" | grep -q "USER-SERVICE"; then
    print_status "SUCCESS" "Services are registered with Eureka"
else
    print_status "WARNING" "Some services may not be registered with Eureka"
fi

print_status "INFO" "🏁 API testing completed!"
print_status "INFO" ""
print_status "INFO" "📊 Service Status Summary:"
print_status "INFO" "   • API Gateway: http://localhost:8080"
print_status "INFO" "   • Eureka Dashboard: http://localhost:8761"
print_status "INFO" "   • Keycloak Admin: http://localhost:8090/admin"
print_status "INFO" ""
print_status "INFO" "Check individual service logs in the 'logs' directory for more details."
