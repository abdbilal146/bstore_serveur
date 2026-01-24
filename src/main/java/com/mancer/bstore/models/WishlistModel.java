package com.mancer.bstore.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wishlist")
public class WishlistModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonIgnore
    private String userId;

    @Column(nullable = false)
    private String productId;

    private String productName;

    private BigDecimal productPrice = BigDecimal.ZERO;

    @Column
    private String image;

    private LocalDateTime addedAt = LocalDateTime.now();

    /*
     * @OneToMany(mappedBy = "wishlistModel", cascade = CascadeType.ALL,
     * orphanRemoval = true)
     * private List<WishlistItemModel> items = new ArrayList<>();
     */
}
