package com.tsinjo.controller;

import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateFoodRequest;
import com.tsinjo.service.FoodService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.service.CategoryService;
import com.tsinjo.service.IngredientsService;
import jakarta.validation.Valid;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.FoodResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/food")
public class AdminFoodController{
    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IngredientsService ingredientsService;
    @Autowired private ApiMapper apiMapper;

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(@Valid @RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwt
                                           ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.findRestaurantById(req.getRestaurantId());
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurant);
        com.tsinjo.model.Category category = categoryService.findCategoryById(req.getCategoryId());
        if (category.getRestaurant() == null || !category.getRestaurant().getId().equals(restaurant.getId())) {
            throw new com.tsinjo.exception.BusinessException("Category does not belong to the selected restaurant");
        }
        java.util.List<com.tsinjo.model.IngredientsItem> ingredients = req.getIngredientIds() == null
                ? java.util.List.of()
                : req.getIngredientIds().stream().distinct().map(ingredientId -> {
                    com.tsinjo.model.IngredientsItem managed = ingredientsService.findIngredientById(ingredientId);
                    if (managed.getRestaurant() == null || !managed.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new com.tsinjo.exception.BusinessException("Ingredient does not belong to the selected restaurant");
                    }
                    return managed;
                }).toList();
        Food food= foodService.createFood(req, category, restaurant, ingredients);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toFoodResponse(food));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireFoodOwnerOrAdmin(user, foodService.findFoodById(id));
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(@PathVariable Long id,
                                                   @Valid @RequestBody CreateFoodRequest req,
                                                   @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Food existing = foodService.findFoodById(id);
        authorizationService.requireFoodOwnerOrAdmin(user, existing);
        Restaurant restaurant = restaurantService.findRestaurantById(req.getRestaurantId());
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurant);
        com.tsinjo.model.Category category = categoryService.findCategoryById(req.getCategoryId());
        if (category.getRestaurant() == null || !category.getRestaurant().getId().equals(restaurant.getId())) {
            throw new com.tsinjo.exception.BusinessException("Category does not belong to the selected restaurant");
        }
        java.util.List<com.tsinjo.model.IngredientsItem> ingredients = req.getIngredientIds() == null
                ? java.util.List.of()
                : req.getIngredientIds().stream().distinct().map(ingredientsService::findIngredientById).peek(ingredient -> {
                    if (ingredient.getRestaurant() == null || !ingredient.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new com.tsinjo.exception.BusinessException("Ingredient does not belong to the selected restaurant");
                    }
                }).toList();
        Food updated = foodService.updateFood(id, req, category, restaurant, ingredients);
        return ResponseEntity.ok(apiMapper.toFoodResponse(updated));
    }


    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFoodAvailabilityStatus(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireFoodOwnerOrAdmin(user, foodService.findFoodById(id));
        Food food= foodService.updateAvailabilityStatus(id);

        return ResponseEntity.ok(apiMapper.toFoodResponse(food));
    }
}

