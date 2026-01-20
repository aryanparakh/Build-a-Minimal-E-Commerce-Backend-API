package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    private final ProductService productService;
    
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody AddToCartRequest request) {
        CartItem cartItem = cartService.addToCart(request);
        return ResponseEntity.ok(cartItem);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserCart(@PathVariable String userId) {
        List<CartItem> cartItems = cartService.getUserCart(userId);
        
        List<Map<String, Object>> response = cartItems.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("quantity", item.getQuantity());
                    
                    Product product = productService.getProductById(item.getProductId());
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", product.getId());
                    productMap.put("name", product.getName());
                    productMap.put("price", product.getPrice());
                    
                    itemMap.put("product", productMap);
                    return itemMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Map<String, String>> clearUserCart(@PathVariable String userId) {
        cartService.clearUserCart(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable String userId, 
                                              @PathVariable String productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{userId}/{productId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable String userId,
                                                      @PathVariable String productId,
                                                      @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
}
