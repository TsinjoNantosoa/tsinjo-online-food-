package com.tsinjo.controller;

import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateFoodRequest;
import com.tsinjo.service.FoodService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private UserService userService;

    private FoodService foodService;

    @Autowired
    private RestaurantService restaurantService;



    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam String name,
                                           @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);

        List<Food> food=foodService.searchFood(name);

        return  new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getRestaurantFood(
                                                 @RequestParam boolean vegetarian,
                                                 @RequestParam boolean seasonal,
                                                 @RequestParam boolean nonVegetarian,
                                                 @RequestParam (required = false) String food_category,
                                                 @PathVariable Long restaurantId,
                                                 @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);

        List<Food> food=foodService.getRestaurantFood(restaurantId,vegetarian,nonVegetarian,seasonal, food_category);

        return  new ResponseEntity<>(food, HttpStatus.OK );
    }
}





































