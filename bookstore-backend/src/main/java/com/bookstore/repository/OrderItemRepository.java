package com.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
