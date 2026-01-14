package com.mancer.bstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.mancer.bstore.filters.ApiClientCredentialsFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final ApiClientCredentialsFilter apiClientCredentialsFilter;

    public SecurityConfig(ApiClientCredentialsFilter apiClientCredentialsFilter) {
        this.apiClientCredentialsFilter = apiClientCredentialsFilter;
    }

    @Bean
    public FilterRegistrationBean<ApiClientCredentialsFilter> registration(ApiClientCredentialsFilter filter) {
        FilterRegistrationBean<ApiClientCredentialsFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/private/**").authenticated() // client + JWT
                        .requestMatchers("/api/**").authenticated() // client seul
                        .anyRequest().denyAll())
                // JWT requis POUR api/private/**
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                // Client ID / Secret requis POUR api/**
                .addFilterBefore(
                        apiClientCredentialsFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
