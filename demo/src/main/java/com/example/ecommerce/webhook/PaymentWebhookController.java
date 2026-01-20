package com.example.ecommerce.webhook;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> handlePaymentWebhook(@RequestBody PaymentWebhookRequest request) {
        log.info("Received payment webhook: {}", request.getEvent());
        
        try {
            if ("payment.captured".equals(request.getEvent())) {
                PaymentWebhookRequest.Payment paymentData = request.getPayload().getPayment();
                Payment payment = paymentService.processPaymentWebhook(
                        paymentData.getId(), 
                        paymentData.getStatus()
                );
                
                log.info("Payment webhook processed successfully. Payment ID: {}, Status: {}", 
                        payment.getId(), payment.getStatus());
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Webhook processed successfully");
                response.put("paymentId", payment.getId());
                response.put("status", payment.getStatus());
                
                return ResponseEntity.ok(response);
            } else {
                log.warn("Unhandled webhook event: {}", request.getEvent());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Event not handled");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error processing payment webhook: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to process webhook: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
