package com.tsinjo.service;

import com.tsinjo.exception.ForbiddenOperationException;
import com.tsinjo.model.*;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    public void requireRestaurantOwnerOrAdmin(User user, Restaurant restaurant) {
        if (user.getRole() == USER_ROLE.ROLE_ADMIN) {
            return;
        }
        if (restaurant.getOwner() == null || !restaurant.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("The authenticated user does not own this restaurant");
        }
    }

    public void requireFoodOwnerOrAdmin(User user, Food food) {
        if (food.getRestaurant() == null) {
            throw new ForbiddenOperationException("Food is not attached to a restaurant");
        }
        requireRestaurantOwnerOrAdmin(user, food.getRestaurant());
    }

    public void requireOrderRestaurantOwnerOrAdmin(User user, Order order) {
        requireRestaurantOwnerOrAdmin(user, order.getRestaurant());
    }
}
