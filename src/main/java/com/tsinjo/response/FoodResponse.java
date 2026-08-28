package com.tsinjo.response;

import java.time.Instant;
import java.util.List;

public record FoodResponse(Long id, String name, String description, Long price,
                           List<String> images, boolean available, boolean vegetarian,
                           boolean seasonal, RestaurantSummaryResponse restaurant,
                           CategorySummaryResponse category,
                           List<IngredientItemResponse> ingredients, Instant creationDate) {
}
