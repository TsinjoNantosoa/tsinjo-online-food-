package com.tsinjo.controller;

import com.tsinjo.model.Food;
import com.tsinjo.service.FoodService;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.FoodResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;
    @Autowired private ApiMapper apiMapper;



    @GetMapping("/search")
    @SecurityRequirements
    public ResponseEntity<List<FoodResponse>> searchFood(@RequestParam String name) {
        List<Food> food=foodService.searchFood(name);
        return ResponseEntity.ok(food.stream().map(apiMapper::toFoodResponse).toList());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @SecurityRequirements
    public ResponseEntity<List<FoodResponse>> getRestaurantFood(
                                                 @RequestParam(defaultValue = "false") boolean vegetarian,
                                                 @RequestParam(defaultValue = "false") boolean seasonal,
                                                 @RequestParam(defaultValue = "false") boolean nonVegetarian,
                                                 @RequestParam (required = false) String food_category,
                                                 @PathVariable Long restaurantId) {
        List<Food> food=foodService.getRestaurantFood(restaurantId,vegetarian,nonVegetarian,seasonal, food_category);
        return ResponseEntity.ok(food.stream().map(apiMapper::toFoodResponse).toList());
    }
}





































