package com.tsinjo.controller;


import com.tsinjo.dto.RestaurantDto;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.RestaurantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired private ApiMapper apiMapper;

    @GetMapping("/search")
    @SecurityRequirements
    public ResponseEntity<List<RestaurantResponse>> searchRestaurant(@RequestParam String keyword) {
        List<Restaurant> restaurant= restaurantService.searchRestaurant(keyword);
        return ResponseEntity.ok(restaurant.stream().map(apiMapper::toRestaurantResponse).toList());
    }


    @GetMapping()
    @SecurityRequirements
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurant() {
        List<Restaurant> restaurant= restaurantService.getAllRestaurant();
        return ResponseEntity.ok(restaurant.stream().map(apiMapper::toRestaurantResponse).toList());
    }



    @GetMapping("/{id}")
    @SecurityRequirements
    public ResponseEntity<RestaurantResponse>findRestaurantById(@PathVariable Long id) throws Exception{
        Restaurant restaurant= restaurantService.findRestaurantById(id);
        return ResponseEntity.ok(apiMapper.toRestaurantResponse(restaurant));
    }



   @PutMapping("/{id}/add-favorite")
    public ResponseEntity<RestaurantDto>addToFavorite(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
       RestaurantDto restaurant=restaurantService.addToFavorites(id, user);
        return  new ResponseEntity<>(restaurant, HttpStatus.OK);
    }


}































