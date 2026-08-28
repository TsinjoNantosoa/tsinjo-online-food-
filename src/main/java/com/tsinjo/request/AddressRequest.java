package com.tsinjo.request;

import com.tsinjo.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {
    @NotBlank @Size(max = 255)
    private String streetAddress;
    @NotBlank @Size(max = 120)
    private String city;
    @Size(max = 120)
    private String state;
    @Size(max = 30)
    private String postalCode;
    @NotBlank @Size(max = 120)
    private String country;

    public Address toAddress() {
        Address address = new Address();
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        return address;
    }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
