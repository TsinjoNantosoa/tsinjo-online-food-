package com.tsinjo.controller;

import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateRestaurantRequest;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
import com.tsinjo.service.AuthorizationService;
import jakarta.validation.Valid;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.RestaurantResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired private ApiMapper apiMapper;

    @PostMapping()
    public ResponseEntity<RestaurantResponse> createRestaurant(
        @Valid @RequestBody CreateRestaurantRequest req,
        @RequestHeader("Authorization") String jwt
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant= restaurantService.createRestaurant(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toRestaurantResponse(restaurant));
    }


    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(id));
        Restaurant restaurant= restaurantService.updateRestaurant(id, req);
        return ResponseEntity.ok(apiMapper.toRestaurantResponse(restaurant));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(id));
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantResponse> updateRestaurantStatus(
//            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(id));
        Restaurant restaurant=restaurantService.updateRestaurantStatus(id);
        return ResponseEntity.ok(apiMapper.toRestaurantResponse(restaurant));
    }



   @GetMapping("/user")
    public ResponseEntity<RestaurantResponse> findRestaurantByUserId(
//            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt

    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.getRestaurantByUserId(user.getId());
        return ResponseEntity.ok(apiMapper.toRestaurantResponse(restaurant));
    }



}
