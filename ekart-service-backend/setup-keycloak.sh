#!/bin/bash

# Keycloak Setup Script for E-Kart
# This script sets up a complete Keycloak configuration for the E-Kart application

set -e  # Exit on any error

echo "🚀 Setting up Keycloak for E-Kart..."

KEYCLOAK_URL="http://localhost:8090"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin123"
REALM_NAME="ekart"
CLIENT_ID="ekart-client"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    print_error "jq is required but not installed. Please install jq first."
    exit 1
fi

# Wait for Keycloak to be ready
print_status "Waiting for Keycloak to be ready..."
RETRY_COUNT=0
MAX_RETRIES=30

until curl -s "$KEYCLOAK_URL/realms/master" >/dev/null 2>&1; do
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        print_error "Keycloak failed to start within expected time"
        exit 1
    fi
    echo "Waiting for Keycloak... ($(($RETRY_COUNT + 1))/$MAX_RETRIES)"
    sleep 5
    RETRY_COUNT=$((RETRY_COUNT + 1))
done

print_success "Keycloak is ready!"

# Get admin token
print_status "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER&password=$ADMIN_PASSWORD&grant_type=password&client_id=admin-cli" | \
  jq -r '.access_token')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    print_error "Failed to get admin token. Check Keycloak admin credentials."
    exit 1
fi

print_success "Admin token obtained"

# Check if realm already exists
print_status "Checking if realm '$REALM_NAME' exists..."
REALM_EXISTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.realm // "null"')

if [ "$REALM_EXISTS" != "null" ]; then
    print_warning "Realm '$REALM_NAME' already exists. Skipping realm creation."
else
    # Create realm
    print_status "Creating E-Kart realm..."
    curl -s -X POST "$KEYCLOAK_URL/admin/realms" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "realm": "'$REALM_NAME'",
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
        "bruteForceProtected": true,
        "accessTokenLifespan": 300,
        "refreshTokenMaxReuse": 0,
        "sslRequired": "none"
      }' > /dev/null

    if [ $? -eq 0 ]; then
        print_success "E-Kart realm created successfully"
    else
        print_error "Failed to create realm"
        exit 1
    fi
fi

# Check if client already exists
print_status "Checking if client '$CLIENT_ID' exists..."
CLIENT_UUID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | \
  jq -r ".[] | select(.clientId==\"$CLIENT_ID\") | .id")

if [ -n "$CLIENT_UUID" ]; then
    print_warning "Client '$CLIENT_ID' already exists. Updating configuration..."
else
    # Create client
    print_status "Creating E-Kart client..."
    curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "clientId": "'$CLIENT_ID'",
        "name": "E-Kart Client",
        "description": "Client for E-Kart microservices",
        "enabled": true,
        "clientAuthenticatorType": "client-secret",
        "redirectUris": ["http://localhost:8080/*", "http://localhost:5173/*", "http://localhost:5174/*"],
        "webOrigins": ["http://localhost:8080", "http://localhost:5173", "http://localhost:5174"],
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
      }' > /dev/null

    if [ $? -eq 0 ]; then
        print_success "E-Kart client created successfully"
        # Get the client UUID for the newly created client
        CLIENT_UUID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
          -H "Authorization: Bearer $ADMIN_TOKEN" \
          -H "Content-Type: application/json" | \
          jq -r ".[] | select(.clientId==\"$CLIENT_ID\") | .id")
    else
        print_error "Failed to create client"
        exit 1
    fi
fi

# Generate new client secret
print_status "Generating new client secret..."
NEW_SECRET_RESPONSE=$(curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients/$CLIENT_UUID/client-secret" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json")

CLIENT_SECRET=$(echo $NEW_SECRET_RESPONSE | jq -r '.value')

if [ "$CLIENT_SECRET" == "null" ] || [ -z "$CLIENT_SECRET" ]; then
    print_error "Failed to generate client secret"
    exit 1
fi

print_success "Client secret generated: $CLIENT_SECRET"

print_success "Client secret generated: $CLIENT_SECRET"

# Get realm-management client ID for role assignments
print_status "Getting realm-management client ID..."
REALM_MGMT_CLIENT_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | \
  jq -r '.[] | select(.clientId=="realm-management") | .id')

if [ -z "$REALM_MGMT_CLIENT_ID" ]; then
    print_error "Failed to get realm-management client ID"
    exit 1
fi

# Get service account user ID for ekart-client
print_status "Getting service account user ID..."
SERVICE_ACCOUNT_USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients/$CLIENT_UUID/service-account-user" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" | jq -r '.id')

if [ -z "$SERVICE_ACCOUNT_USER_ID" ]; then
    print_error "Failed to get service account user ID"
    exit 1
fi

# Assign manage-users role to service account
print_status "Assigning manage-users role to service account..."
MANAGE_USERS_ROLE=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients/$REALM_MGMT_CLIENT_ID/roles/manage-users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json")

curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users/$SERVICE_ACCOUNT_USER_ID/role-mappings/clients/$REALM_MGMT_CLIENT_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "[$MANAGE_USERS_ROLE]" > /dev/null

if [ $? -eq 0 ]; then
    print_success "manage-users role assigned to service account"
else
    print_warning "Failed to assign manage-users role (may already exist)"
fi

# Create realm roles
print_status "Creating realm roles..."
for ROLE in "USER" "ADMIN"; do
    ROLE_EXISTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/roles/$ROLE" \
      -H "Authorization: Bearer $ADMIN_TOKEN" 2>/dev/null | jq -r '.name // "null"')
    
    if [ "$ROLE_EXISTS" == "null" ]; then
        curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/roles" \
          -H "Authorization: Bearer $ADMIN_TOKEN" \
          -H "Content-Type: application/json" \
          -d '{
            "name": "'$ROLE'",
            "description": "'$ROLE' role for E-Kart application"
          }' > /dev/null
        
        if [ $? -eq 0 ]; then
            print_success "Role '$ROLE' created"
        else
            print_warning "Failed to create role '$ROLE'"
        fi
    else
        print_warning "Role '$ROLE' already exists"
    fi
done

# Create admin user (optional)
print_status "Creating admin user..."
ADMIN_EMAIL="admin@ekart.com"
ADMIN_USER_EXISTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?email=$ADMIN_EMAIL" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id // "null"')

if [ "$ADMIN_USER_EXISTS" == "null" ]; then
    curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "'$ADMIN_EMAIL'",
        "email": "'$ADMIN_EMAIL'",
        "firstName": "Admin",
        "lastName": "User",
        "enabled": true,
        "emailVerified": true,
        "credentials": [{
          "type": "password",
          "value": "admin123",
          "temporary": false
        }]
      }' > /dev/null

    if [ $? -eq 0 ]; then
        print_success "Admin user created: $ADMIN_EMAIL"
    else
        print_warning "Failed to create admin user"
    fi
else
    print_warning "Admin user already exists"
fi

# Update configuration files
print_status "Updating configuration files..."

# Update user-service configuration
USER_SERVICE_CONFIG_PATH="config/user-service.yml"
CONFIG_SERVER_PATH="config-server/src/main/resources/config/user-service.yml"

update_config_file() {
    local file_path=$1
    if [ -f "$file_path" ]; then
        # Create backup
        cp "$file_path" "${file_path}.backup.$(date +%Y%m%d_%H%M%S)"
        
        # Update the client secret
        if command -v sed &> /dev/null; then
            # macOS compatible sed
            if [[ "$OSTYPE" == "darwin"* ]]; then
                sed -i '' "s/secret: .*/secret: $CLIENT_SECRET/" "$file_path"
            else
                sed -i "s/secret: .*/secret: $CLIENT_SECRET/" "$file_path"
            fi
            print_success "Updated $file_path with new client secret"
        else
            print_warning "sed not available. Please manually update $file_path with client secret: $CLIENT_SECRET"
        fi
    else
        print_warning "Configuration file $file_path not found"
    fi
}

# Update both configuration files
update_config_file "$USER_SERVICE_CONFIG_PATH"
update_config_file "$CONFIG_SERVER_PATH"

# Final summary
print_success "🎉 Keycloak setup completed successfully!"
echo ""
echo "📋 Configuration Summary:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   🏠 Keycloak URL: $KEYCLOAK_URL"
echo "   🌐 Realm: $REALM_NAME"
echo "   🔧 Client ID: $CLIENT_ID"
echo "   🔐 Client Secret: $CLIENT_SECRET"
echo "   👤 Admin User: $ADMIN_EMAIL / admin123"
echo "   🔗 Admin Console: $KEYCLOAK_URL/admin"
echo "   🔗 Realm URL: $KEYCLOAK_URL/realms/$REALM_NAME"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🛠️  What's been configured:"
echo "   ✅ Realm '$REALM_NAME' created/verified"
echo "   ✅ Client '$CLIENT_ID' created/updated"
echo "   ✅ Client secret generated and configured"
echo "   ✅ Service account permissions set (manage-users)"
echo "   ✅ Realm roles created (USER, ADMIN)"
echo "   ✅ Admin user created"
echo "   ✅ Configuration files updated"
echo ""
echo "🚀 Next steps:"
echo "   1. Restart your user-service if it's running"
echo "   2. Test user registration: curl -X POST http://localhost:8080/api/auth/register ..."
echo "   3. Access Keycloak admin at: $KEYCLOAK_URL/admin (admin/admin123)"
echo ""
print_success "Setup complete! Your E-Kart application is ready to use Keycloak authentication."
