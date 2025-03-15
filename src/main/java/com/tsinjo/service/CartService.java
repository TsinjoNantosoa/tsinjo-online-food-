package com.tsinjo.service;

import com.tsinjo.model.Cart;
import com.tsinjo.model.CartItem;
import com.tsinjo.request.AddCartItemRequest;

public interface CartService {

    public CartItem addItemToCart(AddCartItemRequest req , String jwt) throws Exception;

    public  CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception;

    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception;

    public  Long calculateCartItemTotals(Cart cart) throws  Exception;

    Cart findCartById(Long id) throws Exception;

    public Cart findCartByUserId(Long userId) throws Exception;

    public Cart clearCart(Long UserId)throws Exception;

}
