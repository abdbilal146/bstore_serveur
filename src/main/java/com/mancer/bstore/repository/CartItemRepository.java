package com.mancer.bstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mancer.bstore.models.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
