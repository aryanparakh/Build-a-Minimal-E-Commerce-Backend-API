package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    public CartItem addToCart(AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
        
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        CartItem existingItem = cartRepository.findByUserIdAndProductId(
                request.getUserId(), request.getProductId());
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            return cartRepository.save(existingItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .build();
            return cartRepository.save(cartItem);
        }
    }
    
    public List<CartItem> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }
    
    public void clearUserCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
    
    public void removeFromCart(String userId, String productId) {
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId);
        if (item != null) {
            cartRepository.delete(item);
        }
    }
    
    public void updateCartItemQuantity(String userId, String productId, Integer quantity) {
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId);
        if (item != null) {
            if (quantity <= 0) {
                cartRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartRepository.save(item);
            }
        }
    }
}
