package com.tsinjo.request;

import com.tsinjo.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {
    @NotNull
    private Long restaurantId;

    @NotNull
    @Valid
    private Address deliveryAddress;


    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

}
