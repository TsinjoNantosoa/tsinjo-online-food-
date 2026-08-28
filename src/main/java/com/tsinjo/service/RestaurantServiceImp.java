package com.tsinjo.service;

import com.tsinjo.dto.RestaurantDto;
import com.tsinjo.model.Address;
import com.tsinjo.model.Restaurant;
import com.tsinjo.model.User;
import com.tsinjo.repository.AddressRepository;
import com.tsinjo.repository.RestaurantRepository;
import com.tsinjo.repository.UserRepository;
import com.tsinjo.request.CreateRestaurantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tsinjo.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImp implements RestaurantService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {
        Restaurant restaurant=new Restaurant();
        restaurant.setAddress(req.getAddress().toAddress());
        restaurant.setContactInformation(req.getContactInformation() == null ? null
                : req.getContactInformation().toContactInformation());
        restaurant.setCuisineType(req.getCuisineType());
        restaurant.setDescription(req.getDescription());
        restaurant.setImages(req.getImages() == null ? new java.util.ArrayList<>() : req.getImages());
        restaurant.setName(req.getName());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setOwner(user);

        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updateRestaurant) throws Exception {
        Restaurant restaurant=findRestaurantById(restaurantId);

        if (updateRestaurant.getCuisineType()!=null){
            restaurant.setCuisineType(updateRestaurant.getCuisineType());
        }
        if (updateRestaurant.getDescription()!=null){
            restaurant.setDescription(updateRestaurant.getDescription());
        }
        if (updateRestaurant.getName()!=null){
            restaurant.setName(updateRestaurant.getName());
        }
        if (updateRestaurant.getAddress() != null) {
            restaurant.setAddress(updateRestaurant.getAddress().toAddress());
        }
        if (updateRestaurant.getContactInformation() != null) {
            restaurant.setContactInformation(updateRestaurant.getContactInformation().toContactInformation());
        }
        if (updateRestaurant.getOpeningHours() != null) {
            restaurant.setOpeningHours(updateRestaurant.getOpeningHours());
        }
        if (updateRestaurant.getImages() != null) {
            restaurant.setImages(updateRestaurant.getImages());
        }

        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) throws Exception {

        Restaurant restaurant=findRestaurantById(restaurantId);

        restaurantRepository.delete(restaurant);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurant() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        restaurants.forEach(restaurant -> restaurant.getImages().size());
        return restaurants;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurant> searchRestaurant(String keyword) {
        List<Restaurant> restaurants = restaurantRepository.findBySearchQuery(keyword);
        restaurants.forEach(restaurant -> restaurant.getImages().size());
        return restaurants;
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant findRestaurantById(Long id) throws Exception {
        Optional<Restaurant>opt=restaurantRepository.findById(id);

        if (opt.isEmpty()){
            throw new ResourceNotFoundException("Restaurant not found with id: " + id);
        }
        Restaurant restaurant = opt.get();
        restaurant.getImages().size();
        return restaurant;
    }

    @Override
    @Transactional(readOnly = true)
    public Restaurant getRestaurantByUserId(Long userId) throws Exception {
        Restaurant restaurant=restaurantRepository.findByOwnerId(userId);
        if (restaurant==null){
            throw new ResourceNotFoundException("Restaurant not found for owner id: " + userId);
        }

        restaurant.getImages().size();
        return restaurant;
    }

    @Override
    @Transactional
    public RestaurantDto addToFavorites(Long restaurantId, User user) throws Exception {
        Restaurant restaurant =findRestaurantById(restaurantId);
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));
        RestaurantDto dto=new RestaurantDto();
        dto.setImages(restaurant.getImages());
        dto.setTitle(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setId(restaurantId);

        boolean isFavorited=false;
        List<Restaurant> favorites=managedUser.getFavorites();
        isFavorited = favorites.stream().anyMatch(favorite -> favorite.getId().equals(restaurantId));
        if (isFavorited){
            favorites.removeIf(favorite->favorite.getId().equals(restaurantId));
        }else {
            favorites.add(restaurant);
        }

        userRepository.save(managedUser);
        return dto;
    }

    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
        Restaurant restaurant =findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());

        return restaurantRepository.save(restaurant);
    }
}
