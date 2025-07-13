package com.cesar.Gateway.config;

import com.cesar.Gateway.filter.AuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    private final AuthFilter authFilter;

    public RoutingConfiguration(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Forward requests to correct internal services
            .route(r -> r
                    .path("/users/**")
                    .filters(f -> f.filter(authFilter))
                    .uri("http://localhost:9001"))
            .build();
    }
}