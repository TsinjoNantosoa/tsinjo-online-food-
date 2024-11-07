package com.tsinjo.controller;

import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.request.CreateRestaurantRequest;
import com.tsinjo.response.MessageResponse;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.service.UserService;
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

    @PostMapping()
    public ResponseEntity<Restaurant> createRestaurant(
        @RequestBody CreateRestaurantRequest req,
        @RequestHeader("Authorization") String jwt
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant= restaurantService.createRestaurant(req, user);
        return  new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant= restaurantService.updateRestaurant(id, req);
        return  new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRestaurant(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        restaurantService.deleteRestaurant(id);
        MessageResponse response=new MessageResponse();
        response.setMessage("restaurant deleted with success.....");

        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(
//            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id
    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.updateRestaurantStatus(id);
        return  new ResponseEntity<>(restaurant, HttpStatus.OK);
    }



   @GetMapping("/user")
    public ResponseEntity<Restaurant> findRestaurantByUserId(
//            @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt

    ) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Restaurant restaurant=restaurantService.findRestaurantById(user.getId());
        return  new ResponseEntity<>(restaurant, HttpStatus.OK);
    }



}
