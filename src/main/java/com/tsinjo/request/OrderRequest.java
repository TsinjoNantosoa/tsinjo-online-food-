package com.tsinjo.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    @Valid
    private AddressRequest deliveryAddress;


    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public AddressRequest getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressRequest deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

}
