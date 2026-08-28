package com.tsinjo.controller;

import com.tsinjo.model.Cart;
import com.tsinjo.model.CartItem;
import com.tsinjo.model.User;
import com.tsinjo.request.AddCartItemRequest;
import com.tsinjo.request.UpdateCartItemRequest;
import com.tsinjo.service.CartService;
import com.tsinjo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.response.CartItemResponse;
import com.tsinjo.response.CartResponse;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;
    @Autowired private ApiMapper apiMapper;

    @PostMapping("/cart/items")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @Valid @RequestBody AddCartItemRequest req,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        CartItem cartItem= cartService.addItemToCart(req, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiMapper.toCartItemResponse(cartItem));
    }

    @PutMapping("/cart/items")
    public ResponseEntity<CartItemResponse> updateCartItemRequest(
            @Valid @RequestBody UpdateCartItemRequest req,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        CartItem cartItem=cartService.updateCartItemQuantity(req.getCartItemId(), req.getQuantity(), jwt);
        return ResponseEntity.ok(apiMapper.toCartItemResponse(cartItem));
    }

    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long id,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        cartService.removeItemFromCart(id, jwt);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<CartResponse> clearCart(
            @RequestHeader ("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Cart cart=cartService.clearCart(user.getId());
        cart.setTotal(0L);
        return ResponseEntity.ok(apiMapper.toCartResponse(cart));
    }

   @GetMapping("/cart")
    public ResponseEntity<CartResponse> findUserCart(
            @RequestHeader ("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Cart cart=cartService.findCartByUserId(user.getId());
        return ResponseEntity.ok(apiMapper.toCartResponse(cart));
    }

}































