package com.tsinjo.repository;

import com.tsinjo.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Override
    @EntityGraph(attributePaths = {"images", "owner", "address"})
    List<Restaurant> findAll();

    @Override
    @EntityGraph(attributePaths = {"images", "owner", "address"})
    Optional<Restaurant> findById(Long id);

    @EntityGraph(attributePaths = {"images", "owner", "address"})
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> findBySearchQuery(String query);

    @EntityGraph(attributePaths = {"images", "owner", "address"})
    Restaurant findByOwnerId(Long userId);
}
