package com.mancer.bstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mancer.bstore.models.OrderModel;
import com.mancer.bstore.services.OrderService;

@RestController
@RequestMapping("api/private/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/latest")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<OrderModel> getLatestOrder(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        return orderService.getLastOrder(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<java.util.List<OrderModel>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
}
