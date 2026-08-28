package com.tsinjo.service;

import com.tsinjo.model.*;
import com.tsinjo.repository.CartItemRepository;
import com.tsinjo.repository.CartRepository;
import com.tsinjo.request.AddCartItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImpTest {
    @Mock CartRepository cartRepository;
    @Mock UserService userService;
    @Mock CartItemRepository cartItemRepository;
    @Mock FoodService foodService;
    @InjectMocks CartServiceImp service;

    @Test
    void addsFoodToAuthenticatedUsersCart() throws Exception {
        User user = user(1L);
        Cart cart = cart(10L, user);
        Food food = food(20L, 2500L);
        AddCartItemRequest request = new AddCartItemRequest();
        request.setFoodId(20L);
        request.setQuantity(2);
        when(userService.findUserByJwtToken("Bearer token")).thenReturn(user);
        when(foodService.findFoodById(20L)).thenReturn(food);
        when(cartRepository.findByCustomerId(1L)).thenReturn(cart);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartItem result = service.addItemToCart(request, "Bearer token");

        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getTotalPrice()).isEqualTo(5000L);
        assertThat(cart.getItems()).contains(result);
    }

    @Test
    void updatesOwnedCartItemQuantityAndPrice() throws Exception {
        User user = user(1L);
        Cart cart = cart(10L, user);
        CartItem item = new CartItem();
        item.setId(30L);
        item.setCart(cart);
        item.setFood(food(20L, 1500L));
        when(userService.findUserByJwtToken("Bearer token")).thenReturn(user);
        when(cartItemRepository.findById(30L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(item)).thenReturn(item);

        CartItem result = service.updateCartItemQuantity(30L, 3, "Bearer token");

        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.getTotalPrice()).isEqualTo(4500L);
    }

    private User user(Long id) { User user = new User(); user.setId(id); return user; }
    private Cart cart(Long id, User user) { Cart cart = new Cart(); cart.setId(id); cart.setCustomer(user); cart.setItems(new ArrayList<>()); return cart; }
    private Food food(Long id, Long price) { Food food = new Food(); food.setId(id); food.setPrice(price); return food; }
}
