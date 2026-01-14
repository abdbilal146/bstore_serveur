package com.mancer.bstore.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mancer.bstore.models.OrderModel;
import com.mancer.bstore.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.mancer.bstore.repository.CartRepository cartRepository;

    @Transactional
    public OrderModel addOrder(OrderModel orderModel) {
        // 1. Fetch the cart
        com.mancer.bstore.models.CartModel cart = cartRepository.findById(orderModel.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Convert CartItems to OrderItems
        java.util.List<com.mancer.bstore.models.OrderItem> orderItems = new java.util.ArrayList<>();

        for (com.mancer.bstore.models.CartItem cartItem : cart.getItems()) {
            com.mancer.bstore.models.OrderItem orderItem = new com.mancer.bstore.models.OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductPrice(cartItem.getProductPrice());
            orderItem.setImage(cartItem.getImage());
            orderItem.setQuantity(1);
            orderItem.setOrder(orderModel);

            orderItems.add(orderItem);
        }

        orderModel.setItems(orderItems);

        OrderModel savedOrder = orderRepository.save(orderModel);

        cartRepository.delete(cart);

        return savedOrder;
    }

    public Optional<OrderModel> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<OrderModel> getLastOrder(String userId) {
        return orderRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
    }

    public java.util.List<OrderModel> getUserOrders(String userId) {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
