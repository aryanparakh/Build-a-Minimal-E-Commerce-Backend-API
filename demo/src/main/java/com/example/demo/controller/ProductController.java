package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // Isse Spring ko pata chalta hai ki ye REST API handle karega
@RequestMapping("/api/products") // Base URL jahan ye API hit hogi
public class ProductController {

    @Autowired // Service layer ko connect karne ke liye
    private ProductService productService;

    // POST Request: Naya product create karne ke liye
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    // GET Request: Saare products ki list dekhne ke liye
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }
}
