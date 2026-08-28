package com.tsinjo.repository;

import com.tsinjo.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"restaurant", "restaurant.owner"})
    Optional<Food> findById(Long id);

    List<Food> findByRestaurantId(Long restaurantId);


    @Query("SELECT f FROM Food f LEFT JOIN f.category c WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Food>searchFood(@Param("keyword") String keyword);
}
