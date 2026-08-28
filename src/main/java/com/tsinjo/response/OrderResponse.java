package com.tsinjo.response;

import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, String status, Instant createdAt, Long totalAmount,
                            int totalItems, OrderCustomerResponse customer,
                            RestaurantSummaryResponse restaurant, AddressResponse deliveryAddress,
                            List<OrderItemResponse> items) {
}
