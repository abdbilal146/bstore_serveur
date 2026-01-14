package com.mancer.bstore.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiClientCredentialsFilter extends OncePerRequestFilter {

    @Value("${security.client.id}")
    private String expectedClientId;

    @Value("${security.client.secret}")
    private String expectedClientSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getRequestURI().startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = request.getHeader("X-Client-Id");
        String clientSecret = request.getHeader("X-Client-Secret");

        if (!expectedClientId.equals(clientId) || !expectedClientSecret.equals(clientSecret)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid client credentials");
            return;
        }

        AbstractAuthenticationToken auth = new AbstractAuthenticationToken(
                List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))) {
            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return clientId;
            }
        };

        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
