package com.tsinjo.response;

import java.time.LocalDateTime;
import java.util.List;

public record RestaurantResponse(Long id, String name, String description, String cuisineType,
                                 AddressResponse address, ContactInformationResponse contactInformation,
                                 String openingHours, List<String> images,
                                 LocalDateTime registrationDate, boolean open,
                                 Long ownerId) {
}
