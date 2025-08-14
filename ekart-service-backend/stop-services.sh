#!/bin/bash

# E-Kart Microservices Stop Script

echo "🛑 Stopping E-Kart Microservices..."

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="pids/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            echo "🔴 Stopping $service_name (PID: $pid)..."
            kill "$pid"
            
            # Wait for process to terminate
            local count=0
            while ps -p "$pid" > /dev/null 2>&1 && [ $count -lt 30 ]; do
                sleep 1
                count=$((count + 1))
            done
            
            # Force kill if still running
            if ps -p "$pid" > /dev/null 2>&1; then
                echo "⚠️  Force killing $service_name..."
                kill -9 "$pid"
            fi
        fi
        rm -f "$pid_file"
    else
        echo "⚪ $service_name PID file not found"
    fi
}

# Stop all services
stop_service "notification-service"
stop_service "payment-service"
stop_service "order-service"
stop_service "product-service"
stop_service "user-service"
stop_service "api-gateway"
stop_service "eureka-server"
stop_service "config-server"

# Stop Docker services
echo "🐳 Stopping Docker services..."
docker-compose down

# Clean up
echo "🧹 Cleaning up..."
rm -rf pids logs

echo "✅ All services stopped!"
