package com.example.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookRequest {
    private String event;
    private Payload payload;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private Payment payment;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment {
        private String id;
        private String order_id;
        private String status;
    }
}
