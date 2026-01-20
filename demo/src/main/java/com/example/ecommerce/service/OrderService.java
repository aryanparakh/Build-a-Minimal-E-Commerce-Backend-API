package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public Order createOrderFromCart(CreateOrderRequest request) {
        List<CartItem> cartItems = cartRepository.findByUserId(request.getUserId());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + request.getUserId());
        }
        
        Double totalAmount = 0.0;
        
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }
        
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .totalAmount(totalAmount)
                .status("CREATED")
                .build();
        
        order = orderRepository.save(order);
        
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
            
            OrderItem orderItem = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(order.getId())
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            
            orderItemRepository.save(orderItem);
            
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        cartRepository.deleteByUserId(request.getUserId());
        
        return order;
    }
    
    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
    
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public List<OrderItem> getOrderItems(String orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    @Transactional
    public Order updateOrderStatus(String orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    public Map<String, Object> getOrderWithDetails(String orderId) {
        Order order = getOrderById(orderId);
        List<OrderItem> orderItems = getOrderItems(orderId);
        Payment payment = paymentRepository.findByOrderId(orderId);
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", order.getId());
        response.put("userId", order.getUserId());
        response.put("totalAmount", order.getTotalAmount());
        response.put("status", order.getStatus());
        response.put("createdAt", order.getCreatedAt());
        
        List<Map<String, Object>> items = orderItems.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new java.util.HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("price", item.getPrice());
                    return itemMap;
                })
                .collect(Collectors.toList());
        response.put("items", items);
        
        if (payment != null) {
            Map<String, Object> paymentMap = new java.util.HashMap<>();
            paymentMap.put("id", payment.getId());
            paymentMap.put("status", payment.getStatus());
            paymentMap.put("amount", payment.getAmount());
            paymentMap.put("paymentId", payment.getPaymentId());
            response.put("payment", paymentMap);
        }
        
        return response;
    }
}
