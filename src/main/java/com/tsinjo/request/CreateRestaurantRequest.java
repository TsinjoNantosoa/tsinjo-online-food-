package com.tsinjo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

//@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantRequest {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private  String description;
    @NotBlank
    private String cuisineType;
    @NotNull
    @Valid
    private AddressRequest address;
    @Valid
    private ContactInformationRequest contactInformation;
    @NotBlank
    private String openingHours;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public ContactInformationRequest getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ContactInformationRequest contactInformation) {
        this.contactInformation = contactInformation;
    }

    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<String>images;



}
