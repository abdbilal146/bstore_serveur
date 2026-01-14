package com.mancer.bstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mancer.bstore.models.WishlistModel;

import jakarta.transaction.Transactional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistModel, Long> {

    List<WishlistModel> findByUserId(String userId);

    boolean existsByUserIdAndProductId(String userId, String productId);

    @Transactional
    void deleteByUserIdAndProductId(String userId, String productId);
}
