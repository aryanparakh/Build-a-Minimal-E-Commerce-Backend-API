# E-Commerce Backend API

A minimal e-commerce backend system built with Spring Boot and MongoDB, supporting product management, shopping cart, order processing, and payment integration with webhook callbacks.

## 🏗️ Architecture

```
Client Application (Postman)
    ↓
E-Commerce API (Spring Boot - Port 8080)
    ↓
Payment Gateway (Mock Service)
    ↓
Webhook Endpoint (/api/webhooks/payment)
```

## 📊 Database Schema

### Entities
- **USER**: User information and authentication
- **PRODUCT**: Product catalog with pricing and stock
- **CART_ITEM**: Shopping cart items for users
- **ORDER**: Order information with status tracking
- **ORDER_ITEM**: Individual items within an order
- **PAYMENT**: Payment processing and status

### Relationships
- User → Cart (1:N)
- User → Orders (1:N)
- Order → Payment (1:1)
- Order → Order Items (1:N)

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- MongoDB (local or cloud instance)

### Setup Instructions

1. **Clone and Build**
   ```bash
   cd demo
   mvn clean install
   ```

2. **Configure MongoDB**
   Update `src/main/resources/application.yml`:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb://localhost:27017/ecommerce
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

## 📋 API Endpoints

### Product Management
- `POST /api/products` - Create a new product
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Shopping Cart
- `POST /api/cart/add` - Add item to cart
- `GET /api/cart/{userId}` - Get user's cart
- `DELETE /api/cart/{userId}/clear` - Clear user's cart
- `DELETE /api/cart/{userId}/{productId}` - Remove item from cart
- `PUT /api/cart/{userId}/{productId}?quantity=N` - Update item quantity

### Order Management
- `POST /api/orders` - Create order from cart
- `GET /api/orders/{orderId}` - Get order details
- `GET /api/orders/user/{userId}` - Get user's orders
- `PUT /api/orders/{orderId}/status?status=PAID` - Update order status

### Payment Processing
- `POST /api/payments/create` - Initiate payment
- `GET /api/payments/order/{orderId}` - Get payment by order

### Webhooks
- `POST /api/webhooks/payment` - Payment callback endpoint

## 🧪 Testing with Postman

### Sample Request Flow

1. **Create Products**
   ```json
   POST /api/products
   {
     "name": "Laptop",
     "description": "Gaming Laptop",
     "price": 50000.0,
     "stock": 10
   }
   ```

2. **Add to Cart**
   ```json
   POST /api/cart/add
   {
     "userId": "user123",
     "productId": "prod123",
     "quantity": 2
   }
   ```

3. **Create Order**
   ```json
   POST /api/orders
   {
     "userId": "user123"
   }
   ```

4. **Process Payment**
   ```json
   POST /api/payments/create
   {
     "orderId": "order123",
     "amount": 100000.0
   }
   ```

5. **Check Order Status**
   ```bash
   GET /api/orders/order123
   ```

### Postman Collection Variables
- `userId`: Test user ID (e.g., "user123")
- `productId`: Product ID from creation response
- `orderId`: Order ID from order creation response

## 💳 Payment Integration

### Mock Payment Service
The application includes a mock payment service that:
- Creates payments with "PENDING" status
- Automatically sends webhook after 3 seconds
- Updates order status to "PAID" on successful payment

### Webhook Flow
1. Client initiates payment
2. Payment is created with PENDING status
3. Mock service simulates processing (3 seconds)
4. Webhook is sent to `/api/webhooks/payment`
5. Order status is updated based on payment result

## 🎯 Business Logic

### Order Flow
1. **Cart Validation**: Check cart is not empty
2. **Stock Validation**: Verify product availability
3. **Order Creation**: Create order with "CREATED" status
4. **Stock Update**: Reduce product stock
5. **Cart Clear**: Empty user's cart
6. **Payment Processing**: Initiate payment flow
7. **Status Update**: Update order based on payment result

### Error Handling
- Insufficient stock throws exception
- Empty cart prevents order creation
- Amount mismatch prevents payment processing
- Invalid order status prevents duplicate payments

## 🔧 Configuration

### Application Properties
```yaml
server:
  port: 8080

spring:
  application:
    name: ecommerce-api
  data:
    mongodb:
      uri: mongodb://localhost:27017/ecommerce

mock:
  payment:
    service:
      url: http://localhost:8081
```

## 📦 Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data MongoDB
- Lombok (for boilerplate reduction)
- Spring Boot Starter Test

## 🧪 Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -Pprod
```

## 🎓 Key Concepts Demonstrated

1. **Service-to-Service Communication**: RestTemplate for external API calls
2. **Database Relationships**: One-to-many and many-to-many relationships
3. **Business Logic**: Cart-to-order conversion with stock management
4. **Webhook Pattern**: Asynchronous payment callbacks
5. **Transaction Management**: Atomic operations for order creation
6. **Async Processing**: Non-blocking payment simulation

## 🎁 Bonus Features (Optional Extensions)

- Order History: `GET /api/orders/user/{userId}`
- Order Cancellation: `POST /api/orders/{orderId}/cancel`
- Product Search: `GET /api/products/search?q=laptop`
- Razorpay Integration (replace mock service)

## 📝 Submission Requirements

✅ **Completed Features:**
- Product CRUD operations
- Shopping cart management
- Order creation and management
- Mock payment integration with webhooks
- Order status updates
- Complete API documentation
- Error handling and validation

✅ **Code Quality:**
- Clean, well-structured code
- Proper separation of concerns
- Comprehensive error handling
- Logging for debugging

✅ **Testing:**
- All endpoints tested with Postman
- Complete order flow verified
- Payment webhook functionality confirmed

## 🐛 Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Ensure MongoDB is running
   - Check connection string in application.yml
   - Verify database permissions

2. **Payment Webhook Not Working**
   - Check if application is running on port 8080
   - Verify firewall isn't blocking localhost connections
   - Check application logs for webhook processing

3. **Stock Management Issues**
   - Ensure transactions are properly configured
   - Check for concurrent access issues
   - Verify stock updates are persisted

## 📞 Support

For issues and questions:
1. Check application logs
2. Verify MongoDB connection
3. Test API endpoints individually
4. Review order flow step-by-step
