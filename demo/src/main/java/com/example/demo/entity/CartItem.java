package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cart_items")
public class CartItem {
    @Id
    private String id;
    private String userId;      // Kis user ka cart hai
    private String productId;   // Kaun sa product hai
    private Integer quantity;   // Kitni quantity hai
}