package com.mancer.bstore.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mancer.bstore.client.ERPNextClient;
import com.mancer.bstore.services.ErpClientService;

@RestController
@RequestMapping("api/products")
@CrossOrigin("http://localhost:5173")
public class ErpNextController {

    @Autowired
    private ERPNextClient erpNextClient;
    @Autowired
    private ErpClientService erpClientService;




    @GetMapping("templates")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> getAlltemplates(
        @RequestParam List<String> fields,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ){
        if (fields == null || fields.isEmpty()) {
            return ResponseEntity.badRequest().body("fields parameter is required");
        }

        int limitStart = (page - 1) * size;

        String fieldsToString = fields.stream()
                .map(tag -> "\"" + tag + "\"")
                .collect(Collectors.joining(","));

        return ResponseEntity.ok(erpNextClient.fetchTemplates(fieldsToString, size, limitStart));

    }

    @GetMapping
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> getProducts(
            @RequestParam List<String> fields,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (fields == null || fields.isEmpty()) {
            return ResponseEntity.badRequest().body("fields parameter is required");
        }

        int limitStart = (page - 1) * size;

        String fieldsToString = fields.stream()
                .map(tag -> "\"" + tag + "\"")
                .collect(Collectors.joining(","));

        return ResponseEntity.ok(erpNextClient.fetchProducts(fieldsToString, size, limitStart));
    }

    @GetMapping("/variants")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<String> getVariants(
            @RequestParam List<String> fields,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam String templateName) {
        if (fields == null || fields.isEmpty()) {
            return ResponseEntity.badRequest().body("fields parameter is required");
        }

        int limitStart = (page - 1) * size;

        String fieldsToString = fields.stream()
                .map(tag -> "\"" + tag + "\"")
                .collect(Collectors.joining(","));

        return ResponseEntity.ok(erpNextClient.fetchVariants(templateName, fieldsToString, size, limitStart));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('API_CLIENT')")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String id,
            @RequestParam List<String> fields) {
        return ResponseEntity.ok(erpClientService.getProductById(id, fields));
    }

    /*
     * @GetMapping("/prices")
     * 
     * @PreAuthorize("hasRole('API_CLIENT')")
     * public ResponseEntity<String> getAllPrices() {
     * return ResponseEntity.ok(erpNextClient.fetchAllPrice());
     * }
     */

}
