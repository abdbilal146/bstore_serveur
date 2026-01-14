// package com.mancer.bstore.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.JwtDecoders;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//
// @Configuration
// public class JwtConfig {
//
// @Bean
// public JwtDecoder jwtDecoder() {
// // 1. Utilise le port 3001 (celui du jeton)
// String issuerUri = "http://localhost:3001/oidc";
// NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
//
// // 2. On désactive TOUTES les validations (expiration, issuer, audience)
// // Utile UNIQUEMENT pour le débug local
// jwtDecoder.setJwtValidator(token -> OAuth2TokenValidatorResult.success());
//
// return jwtDecoder;
// }
// }
