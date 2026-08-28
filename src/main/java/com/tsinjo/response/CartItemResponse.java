package com.tsinjo.response;

import java.util.List;

public record CartItemResponse(Long id, Long foodId, String foodName, String foodImage,
                               int quantity, Long unitPrice, Long totalPrice,
                               List<IngredientItemResponse> selectedIngredients) {
}
