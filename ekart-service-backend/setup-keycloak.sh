#!/bin/bash

# Keycloak Setup Script for E-Kart

echo "Setting up Keycloak for E-Kart..."

KEYCLOAK_URL="http://localhost:8090"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin123"

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
until curl -s "$KEYCLOAK_URL/realms/master" >/dev/null 2>&1; do
    echo "Waiting for Keycloak..."
    sleep 5
done

echo "Keycloak is ready!"

# Get admin token
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER&password=$ADMIN_PASSWORD&grant_type=password&client_id=admin-cli" | \
  jq -r '.access_token')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "Failed to get admin token"
    exit 1
fi

echo "Admin token obtained"

# Create realm
echo "Creating E-Kart realm..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realm": "ekart",
    "displayName": "E-Kart Realm",
    "enabled": true,
    "registrationAllowed": true,
    "registrationEmailAsUsername": true,
    "rememberMe": true,
    "verifyEmail": false,
    "loginWithEmailAllowed": true,
    "duplicateEmailsAllowed": false,
    "resetPasswordAllowed": true,
    "editUsernameAllowed": false,
    "bruteForceProtected": true
  }'

echo "E-Kart realm created"

# Create client
echo "🔧 Creating E-Kart client..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/ekart/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "ekart-client",
    "name": "E-Kart Client",
    "description": "Client for E-Kart microservices",
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "secret": "your-client-secret",
    "redirectUris": ["http://localhost:8080/*"],
    "webOrigins": ["http://localhost:8080"],
    "protocol": "openid-connect",
    "publicClient": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": false,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "bearerOnly": false,
    "consentRequired": false,
    "fullScopeAllowed": true
  }'

echo "E-Kart client created"

# Create roles
echo "Creating roles..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/ekart/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "USER",
    "description": "Regular user role"
  }'

curl -s -X POST "$KEYCLOAK_URL/admin/realms/ekart/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ADMIN",
    "description": "Administrator role"
  }'

echo "Roles created"

# Create admin user
echo "👤 Creating admin user..."
ADMIN_USER_RESPONSE=$(curl -s -X POST "$KEYCLOAK_URL/admin/realms/ekart/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@ekart.com",
    "email": "admin@ekart.com",
    "firstName": "Admin",
    "lastName": "User",
    "enabled": true,
    "emailVerified": true,
    "credentials": [{
      "type": "password",
      "value": "admin123",
      "temporary": false
    }]
  }')

echo "Admin user created"

echo "Keycloak setup completed!"
echo ""
echo "Configuration Summary:"
echo "   • Realm: ekart"
echo "   • Client ID: ekart-client"
echo "   • Client Secret: your-client-secret"
echo "   • Admin User: admin@ekart.com / admin123"
echo "   • Keycloak Console: $KEYCLOAK_URL/admin"
echo ""
echo "Update your application.yml files with the correct client secret!"
