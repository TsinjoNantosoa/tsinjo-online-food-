package com.tsinjo.response;

import java.util.List;

public record IngredientCategoryResponse(Long id, String name, List<IngredientItemResponse> ingredients) {
}
