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
                    .path("/auth/**")
                    .uri("http://authentication:9001"))
            .route(r -> r
                .path("/users/**")
                .filters(f -> f.filter(authFilter))
                .uri("http://user:9002"))
            .route(r -> r
                    .path("/products/**")
                    .uri("http://product:9003"))
            .build();
    }
}