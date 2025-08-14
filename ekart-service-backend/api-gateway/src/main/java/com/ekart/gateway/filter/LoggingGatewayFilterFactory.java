package com.ekart.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            logger.info("Gateway Request: {} {}", request.getMethod(), request.getURI());
            
            return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    logger.info("Gateway Response: {} for {} {}", 
                        exchange.getResponse().getStatusCode(),
                        request.getMethod(), 
                        request.getURI());
                })
            );
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}
