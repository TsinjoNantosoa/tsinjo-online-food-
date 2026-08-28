package com.tsinjo.service;

import com.tsinjo.model.Category;
import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.request.CreateFoodRequest;

import java.util.List;

public interface FoodService {

    Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant, List<IngredientsItem> ingredients);

    Food updateFood(Long foodId, CreateFoodRequest req, Category category,
                    Restaurant restaurant, List<IngredientsItem> ingredients) throws Exception;

    void  deleteFood(Long foodId) throws Exception;

    public List<Food> getRestaurantFood(Long restaurantId,
                                        boolean isVegetarian,
                                        boolean isNonVeg,
                                        boolean isSeasonal,
                                        String foodCategory
    );

    public  List<Food> searchFood(String keyword);

    public Food findFoodById(Long foodId) throws  Exception;

    public  Food updateAvailabilityStatus(Long foodId) throws  Exception;
}
