package com.mancer.bstore.services;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mancer.bstore.dto.CheckoutRequest;
import com.mancer.bstore.dto.CheckoutResponse;
import com.mancer.bstore.dto.PaymentIntent;
import com.mancer.bstore.models.CartModel;
import com.mancer.bstore.models.CheckoutModel;
import com.mancer.bstore.models.CheckoutStatus;
import com.mancer.bstore.repository.CheckoutRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private PaymentService paymentService;

    public CheckoutResponse startCheckout(CheckoutRequest request, String userId) throws Exception {

        //
        CartModel cart = cartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart uis Empty");
        }

        BigDecimal subtotal = cart.getTotal();

        BigDecimal shippingCost = calculateShipping(request.shippingMethod());

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.20"));

        BigDecimal total = subtotal.add(shippingCost).add(tax);

        // creat checko

        CheckoutModel checkout = new CheckoutModel();

        checkout.setCartId(request.cartId());
        checkout.setSubtotal(subtotal);
        checkout.setTax(tax);
        checkout.setShippingCost(shippingCost);
        checkout.setTotal(total);
        checkout.setShippingMethod(request.shippingMethod());
        checkout.setShippingAddress(request.shippingAddress());
        checkout.setStatus(CheckoutStatus.PENDING);
        checkout.setCreatedAt(Instant.now());

        checkoutRepository.save(checkout);

        // cerat payment
        /*
         * PaymentIntent intent = paymentService.createPayement(total,
         * request.paymentMethodId());
         */
        PaymentIntent intent = paymentService.createPayment(total, request.paymentMethodId(), userId,
                checkout.getCartId(), checkout.getShippingAddress());

        checkout.setPaymentIntentId(intent.id());
        checkout.setPaymentMethodId(request.paymentMethodId());
        checkout.setStatus(CheckoutStatus.PAYEMENT_PENDING);

        return new CheckoutResponse(
                checkout.getUuid(),
                intent.paymentUrl());

    }

    private BigDecimal calculateShipping(String method) {
        return switch (method) {
            case "EXPRESS" -> new BigDecimal("15.00");
            default -> new BigDecimal("0.00");
        };
    }

}
