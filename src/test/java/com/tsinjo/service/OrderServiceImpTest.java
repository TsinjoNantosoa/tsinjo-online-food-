package com.tsinjo.service;

import com.tsinjo.model.*;
import com.tsinjo.repository.*;
import com.tsinjo.request.OrderRequest;
import com.tsinjo.request.AddressRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {
    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock AddressRepository addressRepository;
    @Mock UserRepository userRepository;
    @Mock RestaurantService restaurantService;
    @Mock CartService cartService;
    @InjectMocks OrderServiceImp service;

    @Test
    void createsAndReturnsOrderThenClearsCart() throws Exception {
        User user = new User(); user.setId(1L); user.setAddresses(new ArrayList<>());
        Restaurant restaurant = new Restaurant(); restaurant.setId(2L); restaurant.setOrders(new ArrayList<>());
        Food food = new Food(); food.setId(3L); food.setPrice(2000L); food.setRestaurant(restaurant);
        CartItem item = new CartItem(); item.setFood(food); item.setQuantity(2); item.setTotalPrice(4000L);
        Cart cart = new Cart(); cart.setItems(new ArrayList<>(java.util.List.of(item)));
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setStreetAddress("1 Test Street"); addressRequest.setCity("Antananarivo"); addressRequest.setCountry("Madagascar");
        OrderRequest request = new OrderRequest(); request.setRestaurantId(2L); request.setDeliveryAddress(addressRequest);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(restaurantService.findRestaurantById(2L)).thenReturn(restaurant);
        when(cartService.findCartByUserId(1L)).thenReturn(cart);
        when(cartService.calculateCartItemTotals(cart)).thenReturn(4000L);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> { Order order = invocation.getArgument(0); order.setId(9L); return order; });

        Order result = service.createOrder(request, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(9L);
        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getOrder()).isSameAs(result);
        verify(cartService).clearCart(1L);
    }
}
