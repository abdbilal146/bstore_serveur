package com.mancer.bstore.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "checkout")
public class CheckoutModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private Long cartId;

    private String paymentIntentId;

    private String paymentMethodId;

    private String shippingMethod;

    private BigDecimal shippingCost;

    @Embedded
    private Address shippingAddress;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private CheckoutStatus status;

    private Instant createdAt;
}
