package com.tsinjo.repository;

import com.tsinjo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.food"})
    List<Order> findByCustomerId(Long userId);

    @EntityGraph(attributePaths = {"items", "items.food"})
    List<Order> findByRestaurantId(Long restaurantId);
}
