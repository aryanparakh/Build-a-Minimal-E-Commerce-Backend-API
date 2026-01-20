package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(String userId); // User ka cart fetch karne ke liye
    void deleteByUserId(String userId);        // Cart khali karne ke liye
}