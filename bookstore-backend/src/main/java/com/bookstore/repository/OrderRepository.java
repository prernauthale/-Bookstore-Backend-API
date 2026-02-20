package com.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // User-specific orders
    List<Order> findByUserEmail(String userEmail);
}
