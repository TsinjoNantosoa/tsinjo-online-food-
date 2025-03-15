package com.tsinjo.controller;

import com.tsinjo.model.CartItem;
import com.tsinjo.model.Order;
import com.tsinjo.model.User;
import com.tsinjo.request.AddCartItemRequest;
import com.tsinjo.request.OrderRequest;
import com.tsinjo.service.OrderService;
import com.tsinjo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
       Order order=orderService.createOrder(orderRequest, user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity <List<Order>> getOrderHistory(
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
       List<Order> orders=orderService.getUsersOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }




}

