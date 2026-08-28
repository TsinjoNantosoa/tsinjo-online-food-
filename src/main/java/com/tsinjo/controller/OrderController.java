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
import jakarta.validation.Valid;
import com.tsinjo.exception.ForbiddenOperationException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
       Order order=orderService.createOrder(orderRequest, user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity <List<Order>> getOrderHistory(
            @RequestHeader("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
       List<Order> orders=orderService.getUsersOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.findOrderById(id);
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("This order does not belong to the authenticated user");
        }
        return ResponseEntity.ok(order);
    }




}

