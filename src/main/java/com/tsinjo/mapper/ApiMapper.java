package com.tsinjo.mapper;

import com.tsinjo.model.*;
import com.tsinjo.response.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ApiMapper {
    public RestaurantResponse toRestaurantResponse(Restaurant restaurant) {
        return new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.getDescription(),
                restaurant.getCuisineType(), toAddressResponse(restaurant.getAddress()),
                restaurant.getContactInformation() == null ? null : new ContactInformationResponse(
                        restaurant.getContactInformation().getEmail(), restaurant.getContactInformation().getMobile(),
                        restaurant.getContactInformation().getTwitter(), restaurant.getContactInformation().getInstagram()),
                restaurant.getOpeningHours(),
                List.copyOf(restaurant.getImages()), restaurant.getRegistrationDate(), restaurant.isOpen(),
                restaurant.getOwner() == null ? null : restaurant.getOwner().getId());
    }

    public FoodResponse toFoodResponse(Food food) {
        RestaurantSummaryResponse restaurant = food.getRestaurant() == null ? null
                : new RestaurantSummaryResponse(food.getRestaurant().getId(), food.getRestaurant().getName());
        CategorySummaryResponse category = food.getCategory() == null ? null
                : new CategorySummaryResponse(food.getCategory().getId(), food.getCategory().getName());
        return new FoodResponse(food.getId(), food.getName(), food.getDescription(), food.getPrice(),
                List.copyOf(food.getImages()), food.isAvailable(), food.isVegetarian(), food.isSeasonal(),
                restaurant, category, food.getIngredients().stream().map(this::toIngredientItemResponse).toList(),
                food.getCreationDate() == null ? null : food.getCreationDate().toInstant());
    }

    public IngredientItemResponse toIngredientItemResponse(IngredientsItem item) {
        return new IngredientItemResponse(item.getId(), item.getName(), item.isStoke(),
                item.getCategory() == null ? null : item.getCategory().getId(),
                item.getCategory() == null ? null : item.getCategory().getName());
    }

    public IngredientCategoryResponse toIngredientCategoryResponse(IngredientCategory category) {
        return new IngredientCategoryResponse(category.getId(), category.getName(),
                category.getIngredientsItems().stream().map(this::toIngredientItemResponse).toList());
    }

    public CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(this::toCartItemResponse).toList();
        return new CartResponse(cart.getId(), cart.getTotal(),
                items.stream().mapToInt(CartItemResponse::quantity).sum(), items);
    }

    public CartItemResponse toCartItemResponse(CartItem item) {
        String image = item.getFood().getImages().isEmpty() ? null : item.getFood().getImages().getFirst();
        return new CartItemResponse(item.getId(), item.getFood().getId(), item.getFood().getName(), image,
                item.getQuantity(), item.getFood().getPrice(), item.getTotalPrice(),
                item.getIngredients().stream().sorted(Comparator.comparing(IngredientsItem::getId))
                        .map(this::toIngredientItemResponse).toList());
    }

    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(item ->
                new OrderItemResponse(item.getId(), item.getFood().getId(), item.getFood().getName(),
                        item.getQuantity(), item.getFood().getPrice(), item.getTotalPrice(),
                        List.copyOf(item.getIngredients()))).toList();
        return new OrderResponse(order.getId(), order.getOrderStatus(),
                order.getCreatedAt() == null ? null : order.getCreatedAt().toInstant(),
                order.getTotalAmount(), order.getTotalItems(),
                new OrderCustomerResponse(order.getCustomer().getId(), order.getCustomer().getFullName(),
                        order.getCustomer().getEmail()),
                new RestaurantSummaryResponse(order.getRestaurant().getId(), order.getRestaurant().getName()),
                toAddressResponse(order.getDeliveryAddress()), items);
    }

    public AddressResponse toAddressResponse(Address address) {
        return address == null ? null : new AddressResponse(address.getId(), address.getStreetAddress(),
                address.getCity(), address.getState(), address.getPostalCode(), address.getCountry());
    }
}
