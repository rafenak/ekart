#!/bin/bash

# E-Kart Microservices Startup Script

echo "🚀 Starting E-Kart Microservices..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start infrastructure services
echo "📦 Starting infrastructure services..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    
    echo "🔧 Starting $service_name on port $port..."
    cd "$service_name" || exit
    
    # Build the service
    mvn clean compile -q
    
    # Start the service in background
    nohup mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    
    # Store PID for later cleanup
    echo $! > "../pids/${service_name}.pid"
    
    cd ..
    
    # Wait a bit for service to start
    sleep 10
}

# Create directories for logs and PIDs
mkdir -p logs pids

# Start services in order
echo "🏗️  Starting microservices..."

start_service "config-server" 8888
start_service "eureka-server" 8761
start_service "api-gateway" 8080
start_service "user-service" 8081
start_service "product-service" 8082
start_service "order-service" 8083
start_service "payment-service" 8084
start_service "notification-service" 8085

echo "✅ All services started!"
echo ""
echo "📋 Service URLs:"
echo "   • Eureka Dashboard: http://localhost:8761"
echo "   • API Gateway: http://localhost:8080"
echo "   • Keycloak Admin: http://localhost:8090/admin (admin/admin123)"
echo "   • Config Server: http://localhost:8888"
echo ""
echo "📁 Logs are available in the 'logs' directory"
echo "🔍 Use 'tail -f logs/<service>.log' to monitor a specific service"
echo ""
echo "🛑 To stop all services, run: ./stop-services.sh"
