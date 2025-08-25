# Keycloak Setup for E-Kart

This document describes how to set up Keycloak authentication for the E-Kart e-commerce application.

## Prerequisites

- Docker and Docker Compose installed
- `jq` command-line JSON processor installed
- `curl` for API calls

### Installing jq (if not already installed)

**macOS:**
```bash
brew install jq
```

**Ubuntu/Debian:**
```bash
sudo apt-get install jq
```

**CentOS/RHEL:**
```bash
sudo yum install jq
```

## Quick Setup

1. **Start Keycloak and dependencies:**
   ```bash
   docker-compose up keycloak postgres keycloak-db -d
   ```

2. **Wait for services to be ready (about 2-3 minutes), then run the setup script:**
   ```bash
   ./setup-keycloak.sh
   ```

3. **Start the microservices:**
   ```bash
   ./start-services.sh
   ```

## What the Setup Script Does

The `setup-keycloak.sh` script automatically configures:

### ✅ Keycloak Realm Configuration
- Creates the `ekart` realm with proper settings
- Configures registration, email settings, and security policies

### ✅ Client Configuration  
- Creates `ekart-client` for microservice authentication
- Generates a secure client secret automatically
- Configures proper redirect URIs and CORS settings
- Enables service account for user management

### ✅ Permissions & Roles
- Assigns `manage-users` role to the service account
- Creates `USER` and `ADMIN` realm roles
- Sets up proper service account permissions

### ✅ Admin User
- Creates an admin user: `admin@ekart.com` / `admin123`
- Sets up proper user attributes and permissions

### ✅ Configuration Updates
- Automatically updates `user-service.yml` configuration files
- Updates both local config and config-server files
- Creates backup files before making changes

## Configuration Details

After running the script, your services will be configured with:

| Setting | Value |
|---------|--------|
| **Keycloak URL** | http://localhost:8090 |
| **Realm** | ekart |
| **Client ID** | ekart-client |
| **Client Secret** | *Auto-generated* |
| **Admin Console** | http://localhost:8090/admin |
| **Admin Credentials** | admin / admin123 |

## Testing the Setup

### 1. Test User Registration
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -H "X-Requested-With: XMLHttpRequest" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

### 2. Test User Login
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -H "X-Requested-With: XMLHttpRequest" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

## Troubleshooting

### Script Fails to Connect
- Ensure Keycloak is running: `docker-compose ps keycloak`
- Check if Keycloak is ready: `curl http://localhost:8090/realms/master`
- Wait a few more minutes for Keycloak to fully initialize

### Permission Denied Errors
- Make the script executable: `chmod +x setup-keycloak.sh`
- Check if `jq` is installed: `which jq`

### Configuration Not Applied
- Check if configuration files were backed up and updated
- Manually restart user-service after running the script
- Verify the new client secret in the log output

### Re-running the Script
The script is idempotent and can be run multiple times safely. It will:
- Skip creating existing resources
- Generate a new client secret each time
- Update configuration files with the new secret
- Preserve existing users and roles

## Manual Verification

You can verify the setup by logging into the Keycloak Admin Console:

1. Go to http://localhost:8090/admin
2. Login with: admin / admin123  
3. Select the `ekart` realm
4. Check:
   - **Clients** → `ekart-client` exists with service account enabled
   - **Roles** → `USER` and `ADMIN` roles exist
   - **Users** → Service account user has `manage-users` role
   - **Users** → `admin@ekart.com` user exists

## File Structure

```
ekart-service-backend/
├── setup-keycloak.sh              # Main setup script
├── config/user-service.yml        # User service configuration  
├── config-server/src/main/resources/config/
│   └── user-service.yml          # Config server configuration
└── docker-compose.yml            # Keycloak and database services
```

## Security Notes

- The script generates a new client secret each time it runs
- Old configuration files are backed up with timestamps
- Default admin credentials should be changed in production
- Consider using environment variables for sensitive data in production

---

For more information, see the main [README.md](../README.md) or [COMPLETE_DOCUMENTATION.md](../COMPLETE_DOCUMENTATION.md).
