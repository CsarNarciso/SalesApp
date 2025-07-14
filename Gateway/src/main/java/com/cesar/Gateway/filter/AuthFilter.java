package com.cesar.Gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {

    private final WebClient webClient;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9001/auth").build();
    }

    public Mono<Void> onError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Only check auth if existing token!
        HttpCookie tokenCookie = exchange.getRequest().getCookies().getFirst("token");

        System.out.println("COOKIE " + tokenCookie);
        if (tokenCookie == null) {
            return onError(exchange);
        }
        String token = tokenCookie.getValue();

        if(token.isEmpty()) {
            return onError(exchange);
        }

        return webClient.get()
            .uri("/check")
            .cookie("token", token)
            .exchangeToMono(clientResponse -> {

                // If successful response
                if(clientResponse.statusCode().is2xxSuccessful())
                    return chain.filter(exchange); // Just return the response normally

                // If not, get original response
                ServerHttpResponse originalResponse = exchange.getResponse();
                // and include in gateway chain response body
                return originalResponse.writeWith(clientResponse.bodyToFlux(DataBuffer.class));
            });
    }
}