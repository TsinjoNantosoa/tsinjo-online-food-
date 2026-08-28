package com.tsinjo.service;

import com.tsinjo.model.Category;
import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.repository.FoodRepository;
import com.tsinjo.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tsinjo.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodServiceImp implements FoodService{

    @Autowired
    private FoodRepository foodRepository;



    @Override
    public Food createFood(CreateFoodRequest req, Category category, Restaurant restaurant) {
        Food food=new Food();
        food.setCategory(category);
        food.setRestaurant(restaurant);
        food.setDescription(req.getDescription());
        food.setImages(req.getImages() == null ? new java.util.ArrayList<>() : req.getImages());
        food.setName(req.getName());
        food.setPrice(req.getPrice());
        food.setIngredients(req.getIngredients() == null ? new java.util.ArrayList<>() : req.getIngredients());
        food.setSeasonal(req.isSeasional());
        food.setVegetarian(req.isVegetarian());
        food.setAvailable(true);
        food.setCreationDate(new java.util.Date());

        Food saveFood=foodRepository.save(food);
        restaurant.getFoods().add(food);

        return saveFood;
    }

    @Override
    @Transactional
    public void deleteFood(Long foodId) throws Exception {
        Food food=findFoodById(foodId);
        if (food.getRestaurant() != null) {
            food.getRestaurant().getFoods().remove(food);
        }
        foodRepository.delete(food);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> getRestaurantFood(Long restaurantId,
                                        boolean isVegetarian,
                                        boolean isNonVeg,
                                        boolean isSeasonal,
                                        String foodCategory) {
        List<Food>foods=foodRepository.findByRestaurantId(restaurantId);
        if (isVegetarian){
            foods=filterByVegetarian(foods, isVegetarian);
        }
        if (isNonVeg){
            foods=filterByNonVeg(foods,isNonVeg);
        }
        if (isSeasonal){
            foods=filterBySeasonal(foods,isSeasonal);
        }
        if (foodCategory!=null && !foodCategory.equals("")){
            foods=filterByCategory(foods, foodCategory);
        }

        foods.forEach(this::initializeCollections);
        return foods;
    }

    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream().filter(food -> {
            if (food.getCategory()!=null){
                return food.getCategory().getName().equals(foodCategory);
            }
            return false;
        }).collect(Collectors.toList());
    }

    private List<Food> filterBySeasonal(List<Food> foods, boolean isSeasonal) {
        return foods.stream().filter(food -> food.isSeasonal()==isSeasonal).collect(Collectors.toList());
    }

    private List<Food> filterByNonVeg(List<Food> foods, boolean isNonVeg) {
        return foods.stream().filter(food -> food.isVegetarian()==false).collect(Collectors.toList());

    }

    private List<Food> filterByVegetarian(List<Food> foods, boolean isVegetarian) {
        return foods.stream().filter(food -> food.isVegetarian()==isVegetarian).collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<Food> searchFood(String keyword) {
        List<Food> foods = foodRepository.searchFood(keyword);
        foods.forEach(this::initializeCollections);
        return foods;
    }

    @Override
    @Transactional(readOnly = true)
    public Food findFoodById(Long foodId) throws Exception {
        Optional<Food>optionalFood=foodRepository.findById(foodId);
        if (optionalFood.isEmpty()){
            throw new ResourceNotFoundException("Food not found with id: " + foodId);
        }
        Food food = optionalFood.get();
        initializeCollections(food);
        return food;
    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        Food food=findFoodById(foodId);
        food.setAvailable(!food.isAvailable());

        return foodRepository.save(food);
    }

    private void initializeCollections(Food food) {
        food.getImages().size();
        food.getIngredients().size();
    }
}
