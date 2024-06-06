package org.froome.gatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoveDuplicateHeadersGatewayFilterFactory extends AbstractGatewayFilterFactory<RemoveDuplicateHeadersGatewayFilterFactory.Config> {

    public RemoveDuplicateHeadersGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();
            log.info("Headers before removal: {}", response.getHeaders());
            response.getHeaders().forEach((key, value) -> {
                if ("Vary".equalsIgnoreCase(key)) {
                    response.getHeaders().remove(key);
                } else {
                    response.getHeaders().put(key, value);
                }
            });
            log.info("Headers after removal: {}", response.getHeaders());
            return chain.filter(exchange);
        };
    }


    public static class Config {
    }
}