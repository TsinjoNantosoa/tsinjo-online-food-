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

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @PostMapping("/cart/items")
    public ResponseEntity<CartItem> addItemToCart(
            @Valid @RequestBody AddCartItemRequest req,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        CartItem cartItem= cartService.addItemToCart(req, jwt);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @PutMapping("/cart/items")
    public ResponseEntity<CartItem> updateCartItemRequest(
            @Valid @RequestBody UpdateCartItemRequest req,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        CartItem cartItem=cartService.updateCartItemQuantity(req.getCartItemId(), req.getQuantity(), jwt);
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long id,
            @RequestHeader ("Authorization") String jwt) throws Exception{
        cartService.removeItemFromCart(id, jwt);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Cart> clearCart(
            @RequestHeader ("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Cart cart=cartService.clearCart(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

   @GetMapping("/cart")
    public ResponseEntity<Cart> findUserCart(
            @RequestHeader ("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwtToken(jwt);
        Cart cart=cartService.findCartByUserId(user.getId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

}































