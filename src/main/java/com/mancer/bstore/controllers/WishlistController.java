package com.mancer.bstore.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mancer.bstore.models.WishlistModel;
import com.mancer.bstore.repository.WishlistRepository;

import java.util.List;

import org.apache.catalina.connector.Response;
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

@RestController
@RequestMapping("/api/private/wishlist")
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> getMethodName(
            @RequestBody WishlistModel wishlistModel,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");

        if (wishlistRepository.existsByUserIdAndProductId(userId, wishlistModel.getProductId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("product is already on the wishlist");
        }

        WishlistModel wishlist = new WishlistModel();

        wishlist.setUserId(userId);
        wishlist.setProductId(wishlistModel.getProductId());
        wishlist.setImage(wishlistModel.getImage());
        wishlist.setProductName(wishlistModel.getProductName());
        wishlist.setProductPrice(wishlistModel.getProductPrice());

        wishlistRepository.save(wishlist); 

        return ResponseEntity.status(HttpStatus.CREATED).body("Item has been created");

    }

    @GetMapping
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<List<WishlistModel>> findByUserId(@AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getClaimAsString("sub");

        List<WishlistModel> wishlist = wishlistRepository.findByUserId(userId);

        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> deletedProductFromWishlist(
            @PathVariable String productId,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("sub");
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.status(HttpStatus.OK).body("Item deleted from wishlist");
    }

}
