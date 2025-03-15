package com.tsinjo.controller;

import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateFoodRequest;
import com.tsinjo.response.MessageResponse;
import com.tsinjo.service.FoodService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
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

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwt
                                           ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.findRestaurantById(req.getRestaurantId());
        Food food= foodService.createFood(req, req.getCategory(), restaurant);

        return  new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        foodService.deleteFood(id);

        MessageResponse res=new MessageResponse();
        res.setMessage("the food is deleted successfully");

        return  new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@PathVariable Long id,
                                                      @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwtToken(jwt);
        Food food= foodService.updateAvailabilityStatus(id);

        MessageResponse res=new MessageResponse();
        res.setMessage("the food is deleted successfully");

        return  new ResponseEntity<>(food, HttpStatus.CREATED);
    }
}

