package com.tsinjo.dto;

import com.tsinjo.model.Category;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.model.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodRequest {
    private Long id;

    private String name;

    private String description;

    private Long price;

    private Category category;


    private List<String> images;

    private boolean available;

    @ManyToOne
    private Restaurant restaurant;

    private boolean isVegetarian;

    private boolean isSeasonal;

    @ManyToMany
    private List<IngredientsItem> ingredients=new ArrayList<>();

    private Date creationDate;
}
