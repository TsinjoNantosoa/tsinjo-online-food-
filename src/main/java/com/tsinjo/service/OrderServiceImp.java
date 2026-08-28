package com.tsinjo.service;

import com.tsinjo.model.*;
import com.tsinjo.repository.*;
import com.tsinjo.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tsinjo.exception.BusinessException;
import com.tsinjo.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImp.class);

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
    @Transactional
    public Order createOrder(OrderRequest order, User user) throws Exception {
        Address shipAddress=order.getDeliveryAddress().toAddress();

        Address saveAddress=addressRepository.save(shipAddress);

        if (!user.getAddresses().contains(saveAddress)) {
            user.getAddresses().add(saveAddress);
            userRepository.save(user);
        }

        Restaurant restaurant=restaurantService.findRestaurantById(order.getRestaurantId());
        Order createOrder=new Order();
        createOrder.setCustomer(user);
        createOrder.setCreatedAt(new Date());
        createOrder.setOrderStatus("PENDING");
        createOrder.setDeliveryAddress(saveAddress);
        createOrder.setRestaurant(restaurant);

        Cart cart=cartService.findCartByUserId(user.getId());
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot create an order from an empty cart");
        }
        boolean containsAnotherRestaurant = cart.getItems().stream()
                .anyMatch(item -> item.getFood().getRestaurant() == null
                        || !item.getFood().getRestaurant().getId().equals(restaurant.getId()));
        if (containsAnotherRestaurant) {
            throw new BusinessException("All cart items must belong to the selected restaurant");
        }
        List<OrderItems> orderItems=new ArrayList<>();
        for (CartItem cartItem:cart.getItems()){
            OrderItems orderItems1=new OrderItems();
            orderItems1.setFood(cartItem.getFood());
            orderItems1.setIngredients(cartItem.getIngredients().stream().map(IngredientsItem::getName).toList());
            orderItems1.setQuantity(cartItem.getQuantity());
            orderItems1.setTotalPrice(cartItem.getTotalPrice());

            orderItems1.setOrder(createOrder);
            orderItems.add(orderItems1);
        }
        Long totalPrice=cartService.calculateCartItemTotals(cart);

        createOrder.setItems(orderItems);
        createOrder.setTotalPrice(totalPrice);
        createOrder.setTotalAmount(totalPrice);
        createOrder.setTotalItems(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum());

        Order saveOrder= orderRepository.save(createOrder);
        restaurant.getOrders().add(saveOrder);
        cartService.clearCart(user.getId());
        log.info("Order {} created for user {} and restaurant {}", saveOrder.getId(), user.getId(), restaurant.getId());
        return saveOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, String orderStatus) throws Exception {
        Order order=findOrderById(orderId);
        String normalizedStatus = orderStatus.toUpperCase(java.util.Locale.ROOT);
        if (java.util.Set.of("PENDING", "OUT_FOR_DELIVERY", "DELIVERED", "COMPLETED", "CANCELLED")
                .contains(normalizedStatus)) {
            order.setOrderStatus(normalizedStatus);
            return orderRepository.save(order);
        }
       throw new BusinessException("Invalid order status");
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) throws Exception {
        Order order=findOrderById(orderId);
        orderRepository.delete(order);

    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUsersOrder(Long userId) throws Exception {
        List<Order> orders = orderRepository.findByCustomerId(userId);
        orders.forEach(this::initializeOrder);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getRestaurantOrder(Long restaurantId, String orderStatus) throws Exception {
        List<Order> orders= orderRepository.findByRestaurantId(restaurantId);
        if (orderStatus!=null) {
            orders=orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }

        orders.forEach(this::initializeOrder);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Order findOrderById(Long orderId) throws Exception {
        Optional<Order>optionalOrder=orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        Order order = optionalOrder.get();
        initializeOrder(order);
        return order;
    }

    private void initializeOrder(Order order) {
        order.getItems().forEach(item -> {
            item.getIngredients().size();
            item.getFood().getImages().size();
            item.getFood().getIngredients().size();
        });
    }
}
