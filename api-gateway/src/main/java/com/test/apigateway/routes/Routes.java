package com.test.apigateway.routes;

import com.test.apigateway.filter.UsernameHeaderFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Value("${user.service.url}")
    private String userServiceUrl;
    @Value("${operation.service.url}")
    private String operationServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/api/v1/users"), HandlerFunctions.http(userServiceUrl))
                .filter(new UsernameHeaderFilter())
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("userServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("opeartion_service")
                .route(RequestPredicates.path("/api/v1/operations"), HandlerFunctions.http(operationServiceUrl))
                .filter(new UsernameHeaderFilter())
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("operationServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later"))
                .build();
    }
}
