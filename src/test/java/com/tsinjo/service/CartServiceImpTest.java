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
    @Mock IngredientsService ingredientsService;
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

    @Test
    void sameFoodWithDifferentIngredientsCreatesDistinctItems() throws Exception {
        User user = user(1L);
        Cart cart = cart(10L, user);
        IngredientsItem cheese = ingredient(1L, "Cheese");
        IngredientsItem avocado = ingredient(2L, "Avocado");
        Food food = food(20L, 2500L);
        food.setIngredients(new ArrayList<>(java.util.List.of(cheese, avocado)));
        mockCart(user, cart, food);
        when(ingredientsService.findIngredientById(1L)).thenReturn(cheese);
        when(ingredientsService.findIngredientById(2L)).thenReturn(avocado);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addItemToCart(request(20L, 1, java.util.List.of(1L)), "Bearer token");
        service.addItemToCart(request(20L, 1, java.util.List.of(2L)), "Bearer token");

        assertThat(cart.getItems()).hasSize(2);
    }

    @Test
    void sameFoodWithSameIngredientsInDifferentOrderMergesItems() throws Exception {
        User user = user(1L);
        Cart cart = cart(10L, user);
        IngredientsItem cheese = ingredient(1L, "Cheese");
        IngredientsItem avocado = ingredient(2L, "Avocado");
        Food food = food(20L, 2500L);
        food.setIngredients(new ArrayList<>(java.util.List.of(cheese, avocado)));
        mockCart(user, cart, food);
        when(ingredientsService.findIngredientById(1L)).thenReturn(cheese);
        when(ingredientsService.findIngredientById(2L)).thenReturn(avocado);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addItemToCart(request(20L, 1, java.util.List.of(1L, 2L)), "Bearer token");
        service.addItemToCart(request(20L, 2, java.util.List.of(2L, 1L)), "Bearer token");

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(3);
    }

    private User user(Long id) { User user = new User(); user.setId(id); return user; }
    private Cart cart(Long id, User user) { Cart cart = new Cart(); cart.setId(id); cart.setCustomer(user); cart.setItems(new ArrayList<>()); return cart; }
    private Food food(Long id, Long price) { Food food = new Food(); food.setId(id); food.setPrice(price); food.setAvailable(true); return food; }
    private IngredientsItem ingredient(Long id, String name) { IngredientsItem item = new IngredientsItem(); item.setId(id); item.setName(name); item.setStoke(true); return item; }
    private AddCartItemRequest request(Long foodId, int quantity, java.util.List<Long> ids) { AddCartItemRequest request = new AddCartItemRequest(); request.setFoodId(foodId); request.setQuantity(quantity); request.setIngredientIds(ids); return request; }
    private void mockCart(User user, Cart cart, Food food) throws Exception { when(userService.findUserByJwtToken("Bearer token")).thenReturn(user); when(foodService.findFoodById(food.getId())).thenReturn(food); when(cartRepository.findByCustomerId(user.getId())).thenReturn(cart); }
}
