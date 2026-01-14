package com.mancer.bstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mancer.bstore.models.OrderModel;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    java.util.Optional<OrderModel> findTopByUserIdOrderByCreatedAtDesc(String userId);

    java.util.List<OrderModel> findAllByUserIdOrderByCreatedAtDesc(String userId);
}
