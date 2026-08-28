package com.tsinjo.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsinjo.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiMapperSerializationTest {
    private final ApiMapper mapper = new ApiMapper();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void foodResponseIsFiniteAndContainsOnlySummaries() throws Exception {
        Restaurant restaurant = restaurant();
        Category category = new Category(); category.setId(2L); category.setName("Burgers"); category.setRestaurant(restaurant);
        IngredientCategory ingredientCategory = new IngredientCategory(); ingredientCategory.setId(3L); ingredientCategory.setName("Extras");
        IngredientsItem cheese = ingredient(4L, "Cheese", ingredientCategory);
        Food food = food(restaurant, category, cheese);

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(mapper.toFoodResponse(food)));

        assertThat(json.at("/restaurant/name").asText()).isEqualTo("Test Restaurant");
        assertThat(json.at("/category/name").asText()).isEqualTo("Burgers");
        assertThat(json.at("/ingredients/0/categoryId").asLong()).isEqualTo(3L);
        assertThat(json.at("/restaurant/owner").isMissingNode()).isTrue();
    }

    @Test
    void ingredientCategoryResponseDoesNotReintroduceCategoryRecursion() throws Exception {
        IngredientCategory category = new IngredientCategory(); category.setId(3L); category.setName("Extras");
        IngredientsItem cheese = ingredient(4L, "Cheese", category);
        category.setIngredientsItems(new ArrayList<>(List.of(cheese)));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(mapper.toIngredientCategoryResponse(category)));

        assertThat(json.at("/ingredients/0/categoryId").asLong()).isEqualTo(3L);
        assertThat(json.at("/ingredients/0/category").isMissingNode()).isTrue();
    }

    @Test
    void restaurantCartAndOrderResponsesSerializeWithoutEntityGraphs() throws Exception {
        Restaurant restaurant = restaurant();
        Category category = new Category(); category.setId(2L); category.setName("Burgers");
        IngredientCategory ingredientCategory = new IngredientCategory(); ingredientCategory.setId(3L); ingredientCategory.setName("Extras");
        IngredientsItem cheese = ingredient(4L, "Cheese", ingredientCategory);
        Food food = food(restaurant, category, cheese);
        User customer = new User(); customer.setId(7L); customer.setFullName("Customer"); customer.setEmail("customer@test.com");
        CartItem cartItem = new CartItem(); cartItem.setId(8L); cartItem.setFood(food); cartItem.setQuantity(2); cartItem.setTotalPrice(30000L); cartItem.setIngredients(new ArrayList<>(List.of(cheese)));
        Cart cart = new Cart(); cart.setId(9L); cart.setTotal(30000L); cart.setItems(new ArrayList<>(List.of(cartItem)));
        Address address = restaurant.getAddress();
        Order order = new Order(); order.setId(10L); order.setCustomer(customer); order.setRestaurant(restaurant); order.setDeliveryAddress(address); order.setCreatedAt(new Date()); order.setOrderStatus("PENDING"); order.setTotalAmount(30000L); order.setTotalItems(2);
        OrderItems orderItem = new OrderItems(); orderItem.setId(11L); orderItem.setFood(food); orderItem.setQuantity(2); orderItem.setTotalPrice(30000L); orderItem.setIngredients(List.of("Cheese")); order.setItems(new ArrayList<>(List.of(orderItem)));

        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(mapper.toRestaurantResponse(restaurant))).isObject()).isTrue();
        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(mapper.toCartResponse(cart))).at("/items/0/foodName").asText()).isEqualTo("Burger");
        assertThat(objectMapper.readTree(objectMapper.writeValueAsString(mapper.toOrderResponse(order))).at("/customer/password").isMissingNode()).isTrue();
    }

    private Restaurant restaurant() {
        User owner = new User(); owner.setId(1L);
        Address address = new Address(); address.setId(5L); address.setStreetAddress("1 Street"); address.setCity("Antananarivo"); address.setCountry("Madagascar");
        Restaurant restaurant = new Restaurant(); restaurant.setId(1L); restaurant.setName("Test Restaurant"); restaurant.setOwner(owner); restaurant.setAddress(address); restaurant.setImages(new ArrayList<>());
        return restaurant;
    }

    private IngredientsItem ingredient(Long id, String name, IngredientCategory category) {
        IngredientsItem item = new IngredientsItem(); item.setId(id); item.setName(name); item.setStoke(true); item.setCategory(category); return item;
    }

    private Food food(Restaurant restaurant, Category category, IngredientsItem ingredient) {
        Food food = new Food(); food.setId(6L); food.setName("Burger"); food.setPrice(15000L); food.setRestaurant(restaurant); food.setCategory(category); food.setAvailable(true); food.setImages(new ArrayList<>()); food.setIngredients(new ArrayList<>(List.of(ingredient))); return food;
    }
}
