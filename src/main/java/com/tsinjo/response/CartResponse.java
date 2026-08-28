package com.tsinjo.response;

import java.util.List;

public record CartResponse(Long id, Long total, int totalItems, List<CartItemResponse> items) {
}
