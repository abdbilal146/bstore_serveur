package com.mancer.bstore.dto;

import java.util.UUID;

import com.mancer.bstore.models.Address;

public record CheckoutRequest(
                Long cartId,
                String paymentMethodId,
                String shippingMethod,
                Address shippingAddress) {
}
