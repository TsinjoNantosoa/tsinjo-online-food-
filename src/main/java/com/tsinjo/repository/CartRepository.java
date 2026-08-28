package com.tsinjo.repository;

import com.tsinjo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface CartRepository extends JpaRepository<Cart,Long> {

    @EntityGraph(attributePaths = {"items", "items.food"})
    Cart findByCustomerId(Long userId);
}

