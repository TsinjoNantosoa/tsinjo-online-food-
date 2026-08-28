package com.tsinjo.service;

import com.tsinjo.model.Category;
import com.tsinjo.model.Restaurant;
import com.tsinjo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tsinjo.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService{

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String name, Long userId) throws Exception {
        Restaurant restaurant=restaurantService.findRestaurantById(userId);
        Category category=new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findCategoryByRestaurantId(Long id) throws Exception {
        restaurantService.findRestaurantById(id);
        return categoryRepository.findByRestaurantId(id);
    }

    @Override
    public Category findCategoryById(Long id) throws Exception {
        Optional<Category> optionalCategory=categoryRepository.findById(id);
        if (optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        return optionalCategory.get();
    }
}
