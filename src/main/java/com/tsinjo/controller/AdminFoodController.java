package com.tsinjo.controller;

import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateFoodRequest;
import com.tsinjo.response.MessageResponse;
import com.tsinjo.service.FoodService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.service.CategoryService;
import com.tsinjo.service.IngredientsService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<Food> createFood(@Valid @RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwt
                                           ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.findRestaurantById(req.getRestaurantId());
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurant);
        com.tsinjo.model.Category category = categoryService.findCategoryById(req.getCategory().getId());
        if (category.getRestaurant() == null || !category.getRestaurant().getId().equals(restaurant.getId())) {
            throw new com.tsinjo.exception.BusinessException("Category does not belong to the selected restaurant");
        }
        java.util.List<com.tsinjo.model.IngredientsItem> ingredients = req.getIngredients() == null
                ? java.util.List.of()
                : req.getIngredients().stream().map(ingredient -> {
                    com.tsinjo.model.IngredientsItem managed = ingredientsService.findIngredientById(ingredient.getId());
                    if (managed.getRestaurant() == null || !managed.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new com.tsinjo.exception.BusinessException("Ingredient does not belong to the selected restaurant");
                    }
                    return managed;
                }).toList();
        req.setIngredients(ingredients);
        Food food= foodService.createFood(req, category, restaurant);

        return  new ResponseEntity<>(food, HttpStatus.CREATED);
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


    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireFoodOwnerOrAdmin(user, foodService.findFoodById(id));
        Food food= foodService.updateAvailabilityStatus(id);

        return ResponseEntity.ok(food);
    }
}

