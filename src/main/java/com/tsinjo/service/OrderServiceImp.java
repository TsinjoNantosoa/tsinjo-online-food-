package com.tsinjo.service;

import com.tsinjo.model.*;
import com.tsinjo.repository.*;
import com.tsinjo.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CartService cartService;

    @Override
    public Order createOrder(OrderRequest order, User user) throws Exception {
        Address shipAddress=order.getDeliveryAddress();

        Address saveAddress=addressRepository.save(shipAddress);

        if (!user.getAddresses().contains(saveAddress)) {
            user.getAddresses().add(saveAddress);
            userRepository.save(user);
        }

        Restaurant restaurant=restaurantService.findRestaurantById(order.getRestaurantId());
        Order createOrder=new Order();
        createOrder.setCustomer(user);
        createOrder.setCreatedAt(new Date());
        createOrder.setOrderStatus("Pending");
        createOrder.setDeliveryAddress(saveAddress);
        createOrder.setRestaurant(restaurant);

        Cart cart=cartService.findCartByUserId(user.getId());
        List<OrderItems> orderItems=new ArrayList<>();
        for (CartItem cartItem:cart.getItems()){
            OrderItems orderItems1=new OrderItems();
            orderItems1.setFood(cartItem.getFood());
            orderItems1.setIngredients(cartItem.getIngredients());
            orderItems1.setQuantity(cartItem.getQuantity());
            orderItems1.setTotalPrice(cartItem.getTotalPrice());

            OrderItems savedOrderItem=orderItemRepository.save(orderItems1);
            orderItems.add(savedOrderItem);
        }
        Long totalPrice=cartService.calculateCartItemTotals(cart);

        createOrder.setItems(orderItems);
        createOrder.setTotalPrice(totalPrice);

        Order saveOrder= orderRepository.save(createOrder);
        restaurant.getOrders().add(saveOrder);




            return null;
    }

    @Override
    public Order updateOrder(Long orderId, String orderStatus) throws Exception {
        Order order=findOrderById(orderId);
        if (orderStatus.equals("OUT_FOR_DELIVERY")
                || orderStatus.equals("DELIVERED")
                || orderStatus.equals("completed")
                || orderStatus.equals("PENDING"))  {
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }
       throw new  Exception("select please the valid status");
    }

    @Override
    public void cancelOrder(Long orderId) throws Exception {
        Order order=findOrderById(orderId);
        orderItemRepository.deleteById(orderId);

    }

    @Override
    public List<Order> getUsersOrder(Long userId) throws Exception {
        return orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantOrder(Long restaurantId, String orderStatus) throws Exception {
        List<Order> orders= orderRepository.findByRestaurantId(restaurantId);
        if (orderStatus!=null) {
            orders=orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }

        return orders;
    }

    @Override
    public Order findOrderById(Long orderId) throws Exception {
        Optional<Order>optionalOrder=orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new Exception("The order isn 't  found");
        }
        return optionalOrder.get();
    }
}
