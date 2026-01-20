package com.example.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "order_items")
public class OrderItem {
    @Id
    private String id;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Double price; // Price at time of order
}
