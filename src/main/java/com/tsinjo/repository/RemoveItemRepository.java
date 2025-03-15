package com.tsinjo.repository;

import com.tsinjo.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoveItemRepository extends JpaRepository<OrderItems, Long> {
}
