package com.test.apigateway.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;

public class UsernameHeaderFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final String USERNAME_HEADER = "X-Username";

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication != null) {
            username = authentication.getName();
        } else {
            username = null;
        }

        if (username != null) {
            ServerRequest mutatedRequest = ServerRequest.from(request)
                    .headers(httpHeaders -> httpHeaders.add(USERNAME_HEADER, username))
                    .build();

            return next.handle(mutatedRequest);
        }

        return next.handle(request);
    }
}

