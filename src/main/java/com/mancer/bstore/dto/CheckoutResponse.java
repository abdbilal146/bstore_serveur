package com.mancer.bstore.dto;

import java.util.UUID;

public record CheckoutResponse(
        UUID checkoUuid,
        String paymentUrl) {

}
