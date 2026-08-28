package com.tsinjo.repository;

import com.tsinjo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserRepository extends JpaRepository<User,Long> {

    @EntityGraph(attributePaths = "addresses")
    User findByEmail(String username);

    boolean existsByEmail(String email);
}

