package com.tsinjo.service;

import com.tsinjo.model.Cart;
import com.tsinjo.model.CartItem;
import com.tsinjo.model.Food;
import com.tsinjo.model.User;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.repository.CartItemRepository;
import com.tsinjo.repository.CartRepository;
import com.tsinjo.request.AddCartItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tsinjo.exception.ResourceNotFoundException;
import com.tsinjo.exception.ForbiddenOperationException;
import com.tsinjo.exception.BusinessException;

import java.util.Optional;

@Service
public class CartServiceImp implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodService foodService;

    @Autowired
    private IngredientsService ingredientsService;

    @Override
    @Transactional
    public CartItem addItemToCart(AddCartItemRequest req, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Food food = foodService.findFoodById(req.getFoodId());
        Cart cart = cartRepository.findByCustomerId(user.getId());
        if (!food.isAvailable()) {
            throw new BusinessException("Food is currently unavailable");
        }
        java.util.List<Long> requestedIds = req.getIngredientIds() == null ? java.util.List.of()
                : req.getIngredientIds().stream().distinct().sorted().toList();
        java.util.List<IngredientsItem> selectedIngredients = requestedIds.stream()
                .map(ingredientsService::findIngredientById)
                .toList();
        java.util.Set<Long> allowedIds = food.getIngredients().stream()
                .map(IngredientsItem::getId).collect(java.util.stream.Collectors.toSet());
        if (!allowedIds.containsAll(requestedIds)) {
            throw new BusinessException("One or more selected ingredients are not available for this food");
        }
        if (selectedIngredients.stream().anyMatch(ingredient -> !ingredient.isStoke())) {
            throw new BusinessException("One or more selected ingredients are out of stock");
        }

        for (CartItem cartItem : cart.getItems()) {
            if (sameConfiguration(cartItem, food.getId(), requestedIds)) {
                int newQuantity = cartItem.getQuantity() + req.getQuantity();
                return updateOwnedCartItemQuantity(cartItem, newQuantity);
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setFood(food);
        cartItem.setCart(cart);
        cartItem.setQuantity(req.getQuantity());
        cartItem.setIngredients(new java.util.ArrayList<>(selectedIngredients));
        cartItem.setTotalPrice(req.getQuantity() * food.getPrice()); // Correction ici

        CartItem saveCartItem = cartItemRepository.save(cartItem);
        cart.getItems().add(saveCartItem);

        return saveCartItem;
    }

    private boolean sameConfiguration(CartItem item, Long foodId, java.util.List<Long> ingredientIds) {
        if (!item.getFood().getId().equals(foodId)) {
            return false;
        }
        java.util.List<Long> existingIds = item.getIngredients().stream()
                .map(IngredientsItem::getId).distinct().sorted().toList();
        return existingIds.equals(ingredientIds);
    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
        }
        CartItem item = cartItemOptional.get();
        if (!item.getCart().getCustomer().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("This cart item does not belong to the authenticated user");
        }
        return updateOwnedCartItemQuantity(item, quantity);
    }

    private CartItem updateOwnedCartItemQuantity(CartItem item, int quantity) {
        item.setQuantity(quantity);
        item.setTotalPrice(item.getFood().getPrice() * quantity);
        return cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartRepository.findByCustomerId(user.getId());

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (cartItemOptional.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
        }
        CartItem item = cartItemOptional.get();
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ForbiddenOperationException("This cart item does not belong to the authenticated user");
        }

        cart.getItems().remove(item);
        return cartRepository.save(cart);
    }

    @Override
    public Long calculateCartItemTotals(Cart cart) throws Exception {
        Long total = 0L;
        for (CartItem cartItem : cart.getItems()) {
            total += cartItem.getFood().getPrice() * cartItem.getQuantity(); // Correction ici
        }
        return total;
    }

    @Override
    public Cart findCartById(Long id) throws Exception {
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if (optionalCart.isEmpty()) {
            throw new ResourceNotFoundException("Cart not found with id: " + id);
        }
        return optionalCart.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Cart findCartByUserId(Long userId) throws Exception {
        Cart cart = cartRepository.findByCustomerId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for user id: " + userId);
        }
        cart.setTotal(calculateCartItemTotals(cart));
        cart.getItems().forEach(item -> {
            item.getIngredients().size();
            item.getFood().getImages().size();
            item.getFood().getIngredients().size();
        });
        return cart;
    }

    @Override
    @Transactional
    public Cart clearCart(Long userId) throws Exception {
        Cart cart = findCartByUserId(userId);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
