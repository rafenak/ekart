#!/bin/bash

# E-Kart API Test Script

API_BASE="http://localhost:8080"
echo "🧪 Testing E-Kart APIs..."

# Test service health
echo "🏥 Checking service health..."
curl -s "$API_BASE/actuator/health" | jq '.'

# Test user registration
echo "👤 Testing user registration..."
REGISTER_RESPONSE=$(curl -s -X POST "$API_BASE/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }')

echo "$REGISTER_RESPONSE" | jq '.'

# Test user login
echo "🔐 Testing user login..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }')

echo "$LOGIN_RESPONSE" | jq '.'

# Extract token (you might need to adjust this based on your actual response structure)
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken // empty')

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo "✅ Login successful, token obtained"
    
    # Test authenticated endpoints
    echo "📦 Testing product retrieval..."
    curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/products" | jq '.'
    
    echo "👥 Testing user profile..."
    curl -s -H "Authorization: Bearer $TOKEN" "$API_BASE/api/users/profile?email=john.doe@example.com" | jq '.'
else
    echo "❌ Login failed or token not found"
fi

echo "🏁 API testing completed!"
