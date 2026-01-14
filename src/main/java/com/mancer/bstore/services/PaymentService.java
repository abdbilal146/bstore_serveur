package com.mancer.bstore.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mancer.bstore.dto.PaymentIntent;
import com.mancer.bstore.models.Address;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentService {

        @Value("${stripe.apiKey}")
        private String stripeApiKey;

        public PaymentIntent createPayment(BigDecimal amount, String paymentMethodId, String userId, Long cartId,
                        Address address)
                        throws Exception {

                Stripe.apiKey = stripeApiKey;

                SessionCreateParams params = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:5173/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                                .setCancelUrl("http://localhost:5173/checkout/cancel")
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .putMetadata("userId", userId)
                                .putMetadata("cartId", String.valueOf(cartId))
                                .putMetadata("fullName", address.getFullName())
                                .putMetadata("street", address.getStreet())
                                .putMetadata("city", address.getCity())
                                .putMetadata("postalCode", address.getPostalCode())
                                .putMetadata("country", address.getCountry())
                                .addLineItem(
                                                SessionCreateParams.LineItem.builder()
                                                                .setQuantity(1L)
                                                                .setPriceData(
                                                                                SessionCreateParams.LineItem.PriceData
                                                                                                .builder()
                                                                                                .setCurrency("eur")
                                                                                                .setUnitAmount(amount
                                                                                                                .multiply(new BigDecimal(
                                                                                                                                100))
                                                                                                                .longValue()) // en
                                                                                                                              // centimes
                                                                                                .setProductData(
                                                                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                                                                                .builder()
                                                                                                                                .setName("Commande Bstore")
                                                                                                                                .build())
                                                                                                .build())
                                                                .build())
                                .build();

                Session session = Session.create(params);

                return new PaymentIntent(
                                session.getId(),
                                session.getUrl());
        }
}
