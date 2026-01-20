package com.example.demo.controller;

import com.example.demo.dto.AddToCartRequest;
import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartItem addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCart(@PathVariable String userId) {
        return cartService.getCartByUserId(userId);
    }

    @DeleteMapping("/{userId}/clear")
    public String clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return "Cart cleared successfully";
    }
}