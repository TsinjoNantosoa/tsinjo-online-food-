package com.tsinjo.controller;

import com.tsinjo.model.Order;
import com.tsinjo.model.User;
import com.tsinjo.service.OrderService;
import com.tsinjo.service.UserService;
import com.tsinjo.service.AuthorizationService;
import com.tsinjo.service.RestaurantService;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private RestaurantService restaurantService;
    @Autowired private ApiMapper apiMapper;

    @GetMapping("/order/restaurant/{id}")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String order_status,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        authorizationService.requireRestaurantOwnerOrAdmin(user, restaurantService.findRestaurantById(id));
        List<Order> orders = orderService.getRestaurantOrder(id, order_status);
        return ResponseEntity.ok(orders.stream().map(apiMapper::toOrderResponse).toList());
    }

    @PutMapping("/orders/{orderId}/status/{orderStatus}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderStatus,
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order existing = orderService.findOrderById(orderId);
        authorizationService.requireOrderRestaurantOwnerOrAdmin(user, existing);
        Order orders = orderService.updateOrder(orderId,orderStatus);
        return ResponseEntity.ok(apiMapper.toOrderResponse(orders));
    }


}
