package com.tsinjo.controller;

import com.tsinjo.model.Category;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.IngredientCategoryRequest;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.service.CategoryService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    public CategoryController(CategoryService categoryService, RestaurantService restaurantService,
                              UserService userService, AuthorizationService authorizationService) {
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<Category> create(@Valid @RequestBody IngredientCategoryRequest request,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(request.getRestaurantId());
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request.getName(), restaurant.getId()));
    }

    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<List<Category>> list(@PathVariable Long restaurantId) throws Exception {
        restaurantService.findRestaurantById(restaurantId);
        return ResponseEntity.ok(categoryService.findCategoryByRestaurantId(restaurantId));
    }
}
