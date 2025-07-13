package com.cesar.Gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {

    private final WebClient webClient;

    @Autowired
    public AuthFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9001/checkAuth").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpCookie tokenCookie = exchange.getRequest().getCookies().getFirst("token");

        if (tokenCookie == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = tokenCookie.getValue();

        return webClient.post()
                .uri("/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode == HttpStatus.UNAUTHORIZED, clientResponse -> Mono.error(new RuntimeException("Invalid Token")))
                .bodyToMono(Void.class)
                .then(chain.filter(exchange));
    }
}