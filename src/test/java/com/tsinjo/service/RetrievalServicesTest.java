package com.tsinjo.service;

import com.tsinjo.model.Food;
import com.tsinjo.model.Restaurant;
import com.tsinjo.repository.FoodRepository;
import com.tsinjo.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrievalServicesTest {
    @Mock RestaurantRepository restaurantRepository;
    @Mock com.tsinjo.repository.AddressRepository addressRepository;
    @Mock com.tsinjo.repository.UserRepository userRepository;
    @InjectMocks RestaurantServiceImp restaurantService;

    @Mock FoodRepository foodRepository;
    @InjectMocks FoodServiceImp foodService;

    @Test
    void returnsRestaurantFoundByOwner() throws Exception {
        Restaurant restaurant = new Restaurant(); restaurant.setId(4L);
        when(restaurantRepository.findByOwnerId(7L)).thenReturn(restaurant);
        assertThat(restaurantService.getRestaurantByUserId(7L)).isSameAs(restaurant);
    }

    @Test
    void returnsFilteredRestaurantFoods() {
        Food vegetarian = new Food(); vegetarian.setVegetarian(true); vegetarian.setSeasonal(true);
        Food meat = new Food(); meat.setVegetarian(false); meat.setSeasonal(true);
        when(foodRepository.findByRestaurantId(4L)).thenReturn(List.of(vegetarian, meat));
        assertThat(foodService.getRestaurantFood(4L, true, false, false, null))
                .containsExactly(vegetarian);
    }
}
