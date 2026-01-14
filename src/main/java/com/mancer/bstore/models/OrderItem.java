package com.mancer.bstore.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @JsonIgnore
    private OrderModel order;

    private String productId;

    private BigDecimal productPrice;

    // Assuming quantity might be implicitly 1 if not in CartItem,
    // but good practice to have it. I won't add it if CartItem lacks it to avoid
    // mapping issues,
    // or I'll just default it to 1.
    private Integer quantity = 1;

    private String image;
}
