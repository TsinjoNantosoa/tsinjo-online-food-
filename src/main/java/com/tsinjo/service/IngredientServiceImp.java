package com.tsinjo.service;

import com.tsinjo.model.IngredientCategory;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.model.Restaurant;
import com.tsinjo.repository.IngredientCategoryRepository;
import com.tsinjo.repository.IngredientItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tsinjo.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImp implements IngredientsService {

    @Autowired
    private IngredientItemRepository ingredientItemRepository;

    @Autowired
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Override
    public IngredientCategory createIngredientsCategory(String name, Long restaurantId) throws Exception {
        Restaurant restaurant=restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category=new IngredientCategory();
        category.setRestaurant(restaurant);
        category.setName(name);

        return ingredientCategoryRepository.save(category);
    }

    @Override
    public IngredientCategory findIngredientsCategoryById(Long id) throws Exception {
        Optional<IngredientCategory>opt=ingredientCategoryRepository.findById(id);
        if (opt.isEmpty()){
            throw new ResourceNotFoundException("Ingredient category not found with id: " + id);
        }
        return opt.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) throws Exception {
        restaurantService.findRestaurantById(id );
        List<IngredientCategory> categories = ingredientCategoryRepository.findByRestaurantId(id);
        categories.forEach(category -> category.getIngredientsItems().size());
        return categories;
    }

    @Override
    public IngredientsItem createIngredientItem(Long restaurantId, String ingredientsName, Long categoryId) throws Exception {
        Restaurant restaurant=restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category=findIngredientsCategoryById(categoryId);
        if (category.getRestaurant() == null || !category.getRestaurant().getId().equals(restaurantId)) {
            throw new com.tsinjo.exception.BusinessException("Ingredient category does not belong to the restaurant");
        }

        IngredientsItem item=new IngredientsItem();
        item.setName(ingredientsName);
        item.setRestaurant(restaurant);
        item.setCategory(category);

        IngredientsItem ingredient=ingredientItemRepository.save(item);
        category.getIngredientsItems().add(ingredient);
        return ingredient;
    }

    @Override
    public List<IngredientsItem> findRestaurantIngredients(Long restaurantId) {

        return ingredientItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientsItem updateStock(Long id) throws Exception {
        IngredientsItem ingredientsItem = findIngredientById(id);
        ingredientsItem.setStoke(!ingredientsItem.isStoke());

        return ingredientItemRepository.save(ingredientsItem);
    }

    @Override
    public IngredientsItem findIngredientById(Long id) {
        return ingredientItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
    }
}
