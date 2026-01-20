package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.createPayment(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getPaymentId());
        response.put("orderId", payment.getOrderId());
        response.put("amount", payment.getAmount());
        response.put("status", payment.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}
