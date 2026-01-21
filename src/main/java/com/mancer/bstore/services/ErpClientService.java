package com.mancer.bstore.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mancer.bstore.client.ERPNextClient;

import tools.jackson.databind.ObjectMapper;

@Service
public class ErpClientService {

    @Autowired
    private ERPNextClient erpNextClient;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProductById(String id, List<String> fields) {
        String json = erpNextClient.fetchProductById(id);
        ObjectMapper mapper = new ObjectMapper();

        try {

            Map<String, Object> productMap = mapper.readValue(json, Map.class);

            Map<String, Object> dataMap = (Map<String, Object>) productMap.get("data");

            Map<String, Object> filtered = new HashMap<>();
            if (dataMap != null) {
                for (String field : fields) {
                    if (dataMap.containsKey(field)) {
                        filtered.put(field, dataMap.get(field));
                    }
                }
            }

            return filtered;

        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing JSON", e);
        }
    }

    public boolean isCustomerExist(String customerName) {
        try {
            String res = erpNextClient.getCostumerById(customerName);
            return res != null && !res.isEmpty();
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            System.err.println("Erreur critique vérification client: " + e.getMessage());
            throw new RuntimeException("Impossible de vérifier l'existence du client", e);
        }
    }
}
