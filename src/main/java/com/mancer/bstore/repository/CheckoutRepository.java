package com.mancer.bstore.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mancer.bstore.models.CheckoutModel;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutModel, UUID> {

}
