/*
 * package com.mancer.bstore.models;
 * 
 * import java.time.LocalDate;
 * import java.time.LocalDateTime;
 * 
 * import jakarta.persistence.Column;
 * import jakarta.persistence.Entity;
 * import jakarta.persistence.GeneratedValue;
 * import jakarta.persistence.GenerationType;
 * import jakarta.persistence.Id;
 * import jakarta.persistence.JoinColumn;
 * import jakarta.persistence.ManyToOne;
 * import jakarta.persistence.Table;
 * import lombok.Data;
 * import lombok.NoArgsConstructor;
 * 
 * @Data
 * 
 * @NoArgsConstructor
 * 
 * @Entity
 * 
 * @Table(name = "wishlist_items")
 * public class WishlistItemModel {
 * 
 * @Id
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY)
 * private Long id;
 * 
 * @ManyToOne
 * 
 * @JoinColumn(name = "wishlist_id")
 * private WishlistModel wishlistModel;
 * 
 * @Column(nullable = false)
 * private Long productId;
 * 
 * private LocalDateTime addAtt = LocalDateTime.now();
 * 
 * }
 */