package com.tsinjo.request;


import com.tsinjo.model.Category;
import com.tsinjo.model.IngredientsItem;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;


public class CreateFoodRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private Long price;
    @NotNull
    private Category category;

    private List<String> images;

    @NotNull
    @Positive
    private Long restaurantId;
    private boolean vegetarian;
    private boolean seasional;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public boolean isSeasional() {
        return seasional;
    }

    public void setSeasional(boolean seasional) {
        this.seasional = seasional;
    }

    public List<IngredientsItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientsItem> ingredients) {
        this.ingredients = ingredients;
    }

    private List<IngredientsItem> ingredients;

}
