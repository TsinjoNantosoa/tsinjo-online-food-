package com.tsinjo.response;

import java.util.List;

public record OrderItemResponse(Long id, Long foodId, String foodName, int quantity,
                                Long unitPrice, Long totalPrice, List<String> ingredients) {
}
