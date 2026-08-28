package com.tsinjo.config;

import com.tsinjo.model.*;
import com.tsinjo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private final UserRepository users;
    private final CartRepository carts;
    private final RestaurantRepository restaurants;
    private final CategoryRepository categories;
    private final IngredientCategoryRepository ingredientCategories;
    private final IngredientItemRepository ingredients;
    private final FoodRepository foods;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(UserRepository users, CartRepository carts, RestaurantRepository restaurants,
                         CategoryRepository categories, IngredientCategoryRepository ingredientCategories,
                         IngredientItemRepository ingredients, FoodRepository foods,
                         PasswordEncoder passwordEncoder) {
        this.users = users;
        this.carts = carts;
        this.restaurants = restaurants;
        this.categories = categories;
        this.ingredientCategories = ingredientCategories;
        this.ingredients = ingredients;
        this.foods = foods;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User customer = ensureUser("React Customer", "customer@test.com", "Customer123!", USER_ROLE.ROLE_CUSTOMER);
        User owner = ensureUser("React Restaurant Owner", "owner@test.com", "Owner123!", USER_ROLE.ROLE_RESTAURANT_OWNER);
        ensureUser("React Admin", "admin@test.com", "Admin123!", USER_ROLE.ROLE_ADMIN);
        if (carts.findByCustomerId(customer.getId()) == null) {
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setTotal(0L);
            carts.save(cart);
        }
        if (restaurants.findByOwnerId(owner.getId()) == null) {
            Restaurant restaurant = new Restaurant();
            restaurant.setOwner(owner);
            restaurant.setName("Tsinjo Food Dev");
            restaurant.setDescription("Development restaurant for the React frontend");
            restaurant.setCuisineType("International");
            restaurant.setOpeningHours("08:00-22:00");
            restaurant.setRegistrationDate(LocalDateTime.now());
            restaurant.setOpen(true);
            Address address = new Address();
            address.setStreetAddress("1 Dev Street");
            address.setCity("Antananarivo");
            address.setCountry("Madagascar");
            restaurant.setAddress(address);
            restaurant = restaurants.save(restaurant);

            Category burgers = new Category();
            burgers.setName("Burgers");
            burgers.setRestaurant(restaurant);
            burgers = categories.save(burgers);

            IngredientCategory extras = new IngredientCategory();
            extras.setName("Extras");
            extras.setRestaurant(restaurant);
            extras = ingredientCategories.save(extras);

            IngredientsItem cheese = ingredient("Cheese", extras, restaurant);
            IngredientsItem avocado = ingredient("Avocado", extras, restaurant);

            Food burger = new Food();
            burger.setName("Classic Burger");
            burger.setDescription("Development sample burger");
            burger.setPrice(15000L);
            burger.setAvailable(true);
            burger.setCategory(burgers);
            burger.setRestaurant(restaurant);
            burger.setCreationDate(new Date());
            burger.setImages(new ArrayList<>());
            burger.setIngredients(new ArrayList<>(List.of(cheese, avocado)));
            foods.save(burger);
            log.info("Development accounts and sample catalogue created");
        }
    }

    private User ensureUser(String fullName, String email, String password, USER_ROLE role) {
        User existing = users.findByEmail(email);
        if (existing != null) return existing;
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return users.save(user);
    }

    private IngredientsItem ingredient(String name, IngredientCategory category, Restaurant restaurant) {
        IngredientsItem item = new IngredientsItem();
        item.setName(name);
        item.setCategory(category);
        item.setRestaurant(restaurant);
        item.setStoke(true);
        return ingredients.save(item);
    }
}
