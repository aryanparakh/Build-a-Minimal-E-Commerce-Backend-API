package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.dto.AddToCartRequest;
import com.example.demo.repository.CartRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Yeh missing tha
public class CartService {

    @Autowired // Yeh sahi se annotated hona chahiye
    private CartRepository cartRepository;

    public CartItem addToCart(AddToCartRequest request) {
        CartItem item = new CartItem();
        item.setUserId(request.getUserId());
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        return cartRepository.save(item);
    }

    public List<CartItem> getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}