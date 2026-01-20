package com.example.ecommerce.service;

import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    
    @Value("${mock.payment.service.url:http://localhost:8081}")
    private String mockPaymentServiceUrl;
    
    public Payment createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrderId()));
        
        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in CREATED status: " + order.getStatus());
        }
        
        if (!order.getTotalAmount().equals(request.getAmount())) {
            throw new RuntimeException("Amount mismatch. Order amount: " + order.getTotalAmount() + 
                    ", Request amount: " + request.getAmount());
        }
        
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status("PENDING")
                .paymentId("pay_mock_" + UUID.randomUUID().toString())
                .build();
        
        payment = paymentRepository.save(payment);
        
        simulateMockPaymentCallback(payment);
        
        return payment;
    }
    
    @Async
    public CompletableFuture<Void> simulateMockPaymentCallback(Payment payment) {
        try {
            Thread.sleep(3000);
            
            String webhookUrl = "http://localhost:8080/api/webhooks/payment";
            
            String payload = String.format(
                "{\"event\":\"payment.captured\",\"payload\":{\"payment\":{\"id\":\"%s\",\"order_id\":\"%s\",\"status\":\"captured\"}}}",
                payment.getPaymentId(),
                payment.getOrderId()
            );
            
            try {
                restTemplate.postForEntity(webhookUrl, payload, String.class);
                log.info("Mock payment webhook sent successfully for payment: {}", payment.getId());
            } catch (Exception e) {
                log.error("Failed to send mock payment webhook: {}", e.getMessage());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mock payment simulation interrupted: {}", e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    public Payment processPaymentWebhook(String paymentId, String status) {
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with paymentId: " + paymentId);
        }
        
        payment.setStatus("SUCCESS".equals(status) ? "SUCCESS" : "FAILED");
        payment = paymentRepository.save(payment);
        
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + payment.getOrderId()));
        
        order.setStatus("SUCCESS".equals(status) ? "PAID" : "FAILED");
        orderRepository.save(order);
        
        return payment;
    }
    
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
