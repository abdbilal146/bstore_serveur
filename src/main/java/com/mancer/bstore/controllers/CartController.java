package com.mancer.bstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mancer.bstore.models.CartItem;
import com.mancer.bstore.models.CartModel;
import com.mancer.bstore.services.CartService;

@RestController
@RequestMapping("/api/private/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> addItemToCart(
            @RequestBody CartItem cartItem,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getClaimAsString("sub");

        cartService.addItemToTheCart(userId, cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body("Item added to cart");
    }

    @GetMapping
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<CartModel> getCartByUserId(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");

        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> deleteItemById(
            @PathVariable Long itemId) {
        cartService.deleteItemFromCart(itemId);
        return ResponseEntity.status(HttpStatus.OK).body("Item deleted");
    }
}
