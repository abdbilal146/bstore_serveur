package com.mancer.bstore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mancer.bstore.client.ERPNextClient;
import com.mancer.bstore.models.Address;
import com.mancer.bstore.models.OrderModel;
import com.mancer.bstore.services.ErpClientService;
import com.mancer.bstore.services.OrderService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session; // Import Session
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/public/api/webhook")
public class WebhookController {

    @Value("${stripe.webhook.secret}")
    private String stripeSecret;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ERPNextClient erpNextClient;

    @Autowired
    private ErpClientService erpClientService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeSecret);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        System.out.println("Received Stripe Event: " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            System.out.println("DEBUG: Processing checkout.session.completed");

            Session session = null;

            if (event.getDataObjectDeserializer().getObject().isPresent()) {
                session = (Session) event.getDataObjectDeserializer().getObject().get();
                System.out.println("DEBUG: Session deserialized successfully. ID: " + session.getId());
            } else {
                System.out.println("WARNING: Default deserialization failed. Attempting manual deserialization...");
                try {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();
                    session = com.stripe.net.ApiResource.GSON.fromJson(rawJson, Session.class);
                    System.out.println("DEBUG: Manual deserialization successful. ID: " + session.getId());
                } catch (Exception e) {
                    System.out.println("ERROR: Manual deserialization failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (session != null) {
                handleCheckoutSession(session);
            }
        }

        return ResponseEntity.ok("Received");
    }

    private void handleCheckoutSession(Session session) {
        try {

            if (session.getMetadata() == null) {
                System.out.println("ERROR: Metadata map is NULL in session " + session.getId());
                return;
            }

            String userIdStr = session.getMetadata().get("userId");
            String cartIdStr = session.getMetadata().get("cartId");

            System.out.println("DEBUG: Extracted metadata - userId: " + userIdStr + ", cartId: " + cartIdStr);

            if (userIdStr == null || cartIdStr == null) {
                System.out.println("ERROR: Metadata missing in Session. UserID: " + userIdStr);
                return;
            }

            String userId = userIdStr;
            Long cartId = Long.parseLong(cartIdStr);

            OrderModel orderModel = new OrderModel();
            orderModel.setUserId(userId);
            orderModel.setCartId(cartId);

            orderService.addOrder(orderModel);

            // create the adderss

            Address address = new Address();
            address.setFullName(session.getMetadata().get("fullName"));

          
            boolean exists = false;
            try {
                exists = erpClientService.isCustomerExist(userId);
            } catch (Exception e) {
               
            }

            if (!exists) {
                try {
                    erpNextClient.addCustomer(userId, address);
                } catch (Exception e) {
                    if (e.getMessage().contains("Duplicate") || e.getMessage().contains("1062")) {
                        System.out.println("Le client existait déjà (concurrence ou erreur check), on continue.");
                    } else {
                        throw e; 
                    }
                }
            }
            // add address of teh customler to the erp

            address.setCity(session.getMetadata().get("city"));
            address.setCountry(session.getMetadata().get("country"));
            address.setPostalCode(session.getMetadata().get("postalCode"));
            address.setStreet(session.getMetadata().get("street"));

            String addressName = erpNextClient.addAddress(userId, address);

            erpNextClient.addOrder(userIdStr, addressName, orderModel.getItems(), Long.toString(orderModel.getId()),
                    "Bstore");

            System.out.println("✅ SUCCESS: Order created for userId=" + userId);

        } catch (Exception e) {
            System.out.println("❌ ERROR processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}