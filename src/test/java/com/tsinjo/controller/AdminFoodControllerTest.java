package com.tsinjo.controller;

import com.tsinjo.mapper.ApiMapper;
import com.tsinjo.model.*;
import com.tsinjo.request.CreateFoodRequest;
import com.tsinjo.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFoodControllerTest {
    @Mock FoodService foodService;
    @Mock UserService userService;
    @Mock RestaurantService restaurantService;
    @Mock AuthorizationService authorizationService;
    @Mock CategoryService categoryService;
    @Mock IngredientsService ingredientsService;
    @Spy ApiMapper apiMapper;
    @InjectMocks AdminFoodController controller;

    @Test
    void createFoodResolvesCategoryAndIngredientIds() throws Exception {
        User owner = new User(); owner.setId(1L); owner.setRole(USER_ROLE.ROLE_RESTAURANT_OWNER);
        Restaurant restaurant = new Restaurant(); restaurant.setId(10L); restaurant.setName("Restaurant"); restaurant.setOwner(owner);
        Category category = new Category(); category.setId(20L); category.setName("Burgers"); category.setRestaurant(restaurant);
        IngredientCategory ingredientCategory = new IngredientCategory(); ingredientCategory.setId(30L); ingredientCategory.setName("Extras");
        IngredientsItem ingredient = new IngredientsItem(); ingredient.setId(40L); ingredient.setName("Cheese"); ingredient.setCategory(ingredientCategory); ingredient.setRestaurant(restaurant);
        Food saved = new Food(); saved.setId(50L); saved.setName("Burger"); saved.setPrice(15000L); saved.setRestaurant(restaurant); saved.setCategory(category); saved.setIngredients(new ArrayList<>(List.of(ingredient)));
        CreateFoodRequest request = new CreateFoodRequest(); request.setName("Burger"); request.setDescription("Test"); request.setPrice(15000L); request.setRestaurantId(10L); request.setCategoryId(20L); request.setIngredientIds(List.of(40L));
        when(userService.findUserByJwtToken("Bearer token")).thenReturn(owner);
        when(restaurantService.findRestaurantById(10L)).thenReturn(restaurant);
        when(categoryService.findCategoryById(20L)).thenReturn(category);
        when(ingredientsService.findIngredientById(40L)).thenReturn(ingredient);
        when(foodService.createFood(request, category, restaurant, List.of(ingredient))).thenReturn(saved);

        var response = controller.createFood(request, "Bearer token");

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().id()).isEqualTo(50L);
        verify(foodService).createFood(request, category, restaurant, List.of(ingredient));
    }
}
