package com.tsinjo.dto;

import com.tsinjo.model.IngredientsItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FoodResponse {
    private String name;

    private String description;

    private Long price;

    private Long categoryId;


    private List<String> images;

    private boolean available;


    private Long restaurantId;

    private boolean isVegetarian;

    private boolean isSeasonal;

    private List<IngredientsItem> ingredients=new ArrayList<>();

    private Instant creationDate;

    private Instant lastModifiedDate;
}
