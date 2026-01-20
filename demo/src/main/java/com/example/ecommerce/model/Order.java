package com.example.ecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private Double totalAmount;
    private String status; // CREATED, PAID, FAILED, CANCELLED
    private Instant createdAt;
    
    public Order() {
        this.createdAt = Instant.now();
        this.status = "CREATED";
    }
}
