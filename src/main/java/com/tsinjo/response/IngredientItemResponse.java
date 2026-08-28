package com.tsinjo.response;

public record IngredientItemResponse(Long id, String name, boolean inStock,
                                     Long categoryId, String categoryName) {
}
