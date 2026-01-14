package com.mancer.bstore.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mancer.bstore.models.CartItem;
import com.mancer.bstore.models.CartModel;
import com.mancer.bstore.repository.CartItemRepository;
import com.mancer.bstore.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public CartModel getCartByUserId(
            String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CartModel cart = new CartModel();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });

    }

    public CartModel addItemToTheCart(
            String userId,
            CartItem cartItem) {

        BigDecimal itemPrice = cartItem.getProductPrice();

        CartModel cart = getCartByUserId(userId);

        cartItem.setCart(cart);

        List<CartItem> cartItems = cart.getItems();

        cartItems.add(cartItem);

        cart.setItems(cartItems);

        BigDecimal cartTotalPrice = cart.getTotal().add(itemPrice);
        cart.setTotal(cartTotalPrice);

        return cartRepository.save(cart);
    }

    public void deleteItemFromCart(
            Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
