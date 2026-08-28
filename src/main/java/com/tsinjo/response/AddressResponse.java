package com.tsinjo.response;

public record AddressResponse(Long id, String streetAddress, String city, String state,
                              String postalCode, String country) {
}
