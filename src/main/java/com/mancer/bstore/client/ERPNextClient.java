package com.mancer.bstore.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mancer.bstore.models.Address;
import com.mancer.bstore.models.OrderItem;

import reactor.netty.http.client.HttpClient;

@Component
public class ERPNextClient {

        private final WebClient webClient;

        public ERPNextClient(
                        @Value("${erpnext.base-url}") String baseUrl,
                        @Value("${erpnext.token}") String token) {

                HttpClient httpClient = HttpClient.create()
                                .doOnRequest((req, conn) -> req.requestHeaders().remove("Expect"));

                this.webClient = WebClient.builder()
                                .baseUrl(baseUrl)
                                .defaultHeader(HttpHeaders.AUTHORIZATION, token)
                                .defaultHeader("Content-Type", "application/json")
                                .clientConnector(new ReactorClientHttpConnector(httpClient))
                                .build();
        }

        public String fetchProducts(String fields) {
                return webClient.get()
                                .uri("api/resource/Item?fields=[" + fields + "]")
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        }

        public String fetchProductById(String id) {
                return webClient.get()
                                .uri("api/resource/Item/" + id)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        }

        public String addCustomer(String userId, Address address) {
                String body = "{"
                                + "\"name\": \"" + userId + "\","
                                + "\"customer_name\": \"" + address.getFullName() + "\","
                                + "\"customer_group\": \"All Customer Groups\","
                                + "\"territory\": \"All Territories\","
                                + "\"email_id\": \"" + "mancer@gmail.com" + "\","
                                + "\"phone\": \"" + "06454545" + "\""
                                + "}";

                return webClient.post()
                                .uri("api/resource/Customer")
                                .bodyValue(body)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

        }

        // to add address
        public String addAddress(String userId, Address address) {
                String addressName = userId + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("name", addressName);
                requestBody.put("address_title", address.getStreet());
                requestBody.put("address_type", "Shipping");
                requestBody.put("address_line1", address.getStreet());
                requestBody.put("city", address.getCity());
                requestBody.put("state", address.getCity());
                requestBody.put("country", address.getCountry());
                requestBody.put("pincode", address.getPostalCode());

                Map<String, String> customerLink = new HashMap<>();
                customerLink.put("link_doctype", "Customer");
                customerLink.put("link_name", userId);

                requestBody.put("links", List.of(customerLink));

                try {
                        webClient.post()
                                        .uri("api/resource/Address")
                                        .bodyValue(requestBody)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block();
                        return addressName;
                } catch (WebClientResponseException e) {

                        System.err.println("ERPNext Error: " + e.getResponseBodyAsString());
                        throw new RuntimeException("Failed to add address: " + e.getResponseBodyAsString());
                }
        }

        public String getCostumerById(String userId) {
                return webClient.get()
                                .uri("api/resource/" + userId)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        }

        // this method to add Ordes

        public String addOrder(
                        String userId,
                        String addressName,
                        List<OrderItem> items,
                        String orderId,
                        String company) {

                try {
                        Map<String, Object> requestBody = new HashMap<>();

                        // 🔹 Sales Order header
                        requestBody.put("name", orderId);
                        requestBody.put("customer", userId);
                        requestBody.put("transaction_date", LocalDate.now().toString());
                        requestBody.put("delivery_date", "2026-01-20");
                        requestBody.put("order_type", "Sales");
                        requestBody.put("selling_price_list", "Standard Selling");
                        requestBody.put("currency", "EUR");
                        requestBody.put("company", company);

                        // 🔹 Addresses (MUST be Address.name)
                        requestBody.put("customer_address", addressName);
                        requestBody.put("shipping_address_name", addressName);

                        // 🔹 Items
                        List<Map<String, Object>> itemList = new ArrayList<>();

                        for (OrderItem item : items) {
                                Map<String, Object> itemMap = new HashMap<>();
                                itemMap.put("item_code", item.getProductId());
                                itemMap.put("qty", "1");
                                itemMap.put("rate", "item.getRate");
                                itemMap.put("warehouse", "Stores - BS");

                                itemList.add(itemMap);
                        }

                        requestBody.put("items", itemList);

                        // 🔹 POST to ERPNext
                        return webClient.post()
                                        .uri("api/resource/Sales Order")
                                        .bodyValue(requestBody)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                        .block();
                } catch (WebClientResponseException e) {

                        System.err.println("ERPNext Error: " + e.getResponseBodyAsString());
                        throw new RuntimeException("Failed to add address: " + e.getResponseBodyAsString());
                }
        }

        /*
         * public String fetchAllPrice() {
         * return webClient.get()
         * .uri("api/resource/Item Price?fields=[\"item_code\",\"price_list\",\"price_list_rate\"]"
         * )
         * .retrieve()
         * .bodyToMono(String.class)
         * .block();
         * }
         */

}
