package com.tsinjo.controller;

import com.tsinjo.model.Category;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.IngredientCategoryRequest;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.service.CategoryService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import com.tsinjo.response.CategorySummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

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
    public ResponseEntity<CategorySummaryResponse> create(@Valid @RequestBody IngredientCategoryRequest request,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(request.getRestaurantId());
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurant);
        Category category = categoryService.createCategory(request.getName(), restaurant.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CategorySummaryResponse(category.getId(), category.getName()));
    }

    @GetMapping("/restaurants/{restaurantId}/categories")
    @SecurityRequirements
    public ResponseEntity<List<com.tsinjo.response.CategorySummaryResponse>> list(@PathVariable Long restaurantId) throws Exception {
        restaurantService.findRestaurantById(restaurantId);
        return ResponseEntity.ok(categoryService.findCategoryByRestaurantId(restaurantId).stream()
                .map(category -> new com.tsinjo.response.CategorySummaryResponse(category.getId(), category.getName())).toList());
    }
}
