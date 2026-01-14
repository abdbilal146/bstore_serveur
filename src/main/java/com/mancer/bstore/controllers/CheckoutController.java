package com.mancer.bstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mancer.bstore.dto.CheckoutRequest;
import com.mancer.bstore.dto.CheckoutResponse;
import com.mancer.bstore.services.CheckoutService;

@RestController
@RequestMapping("api/private/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request,
            @AuthenticationPrincipal Jwt jwt) throws Exception {

        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(checkoutService.startCheckout(request, userId));
    }
}
