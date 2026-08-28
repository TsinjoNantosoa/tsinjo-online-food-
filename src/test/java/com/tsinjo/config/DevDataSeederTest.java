package com.tsinjo.config;

import com.tsinjo.model.USER_ROLE;
import com.tsinjo.repository.RestaurantRepository;
import com.tsinjo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DevDataSeederTest {
    @Autowired UserRepository users;
    @Autowired RestaurantRepository restaurants;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void devProfileCreatesUsableAccountsAndOwnerCatalogue() {
        var customer = users.findByEmail("customer@test.com");
        var owner = users.findByEmail("owner@test.com");
        var admin = users.findByEmail("admin@test.com");
        assertThat(customer.getRole()).isEqualTo(USER_ROLE.ROLE_CUSTOMER);
        assertThat(owner.getRole()).isEqualTo(USER_ROLE.ROLE_RESTAURANT_OWNER);
        assertThat(admin.getRole()).isEqualTo(USER_ROLE.ROLE_ADMIN);
        assertThat(passwordEncoder.matches("Customer123!", customer.getPassword())).isTrue();
        assertThat(restaurants.findByOwnerId(owner.getId())).isNotNull();
    }
}
