# D3: Place Order & Atomic Checkout Sequence Diagram — SahanaMart

Anna University R2025 Semester 3 CSE Capstone Deliverable.  
**Student:** Sahana D  
**Feature:** Atomic Checkout, Mock Payment Confirmation, and Inventory Deduction (F5)  

---

## 1. Sequence Overview

This diagram documents the end-to-end execution flow of the **Checkout & Order Placement** journey. It highlights:
- Server-side authentication and cart verification.
- Transaction management using JDBC connection boundaries (`setAutoCommit(false)`, `commit()`, and `rollback()`).
- Atomic stock check and deduction preventing overselling race conditions.
- Order and order item batch insertion.
- Cart cleanup and IDOR-safe redirection to the Order Confirmation page.

---

## 2. Mermaid Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as Buyer (Browser)
    participant Filter as AuthFilter
    participant Servlet as CheckoutServlet
    participant OrderSvc as OrderServiceImpl
    participant CartDAO as CartDAOImpl
    participant ProdDAO as ProductDAOImpl
    participant OrderDAO as OrderDAOImpl
    participant DB as HikariCP / Database

    Buyer->>Filter: POST /checkout/place-order (shippingAddress, paymentMethod)
    Filter->>Filter: Verify session currentUser != null
    Filter->>Servlet: forward authorized request
    
    Servlet->>OrderSvc: checkout(buyerId, address, paymentMethod)
    
    OrderSvc->>CartDAO: findByUserId(buyerId)
    CartDAO-->>OrderSvc: List of CartItems
    
    alt Cart is empty
        OrderSvc-->>Servlet: throw ValidationException("Cart is empty")
        Servlet-->>Buyer: Redirect /cart with error alert
    else Cart has items
        loop For each CartItem
            OrderSvc->>ProdDAO: findById(productId)
            ProdDAO-->>OrderSvc: Product entity
            OrderSvc->>OrderSvc: Verify isActive == true and stock >= item.quantity
        end
        
        OrderSvc->>OrderDAO: createOrder(order, orderItems)
        
        Note over OrderDAO,DB: Begin Database Transaction Boundary
        OrderDAO->>DB: conn.setAutoCommit(false)
        
        loop For each item in order
            OrderDAO->>DB: SELECT stock_qty, name FROM products WHERE id = ? FOR UPDATE
            DB-->>OrderDAO: current stock
            alt Stock insufficient
                OrderDAO->>DB: conn.rollback()
                OrderDAO-->>OrderSvc: throw ValidationException("Insufficient stock")
                OrderSvc-->>Servlet: Forward error
                Servlet-->>Buyer: Render checkout error
            else Stock sufficient
                OrderDAO->>DB: UPDATE products SET stock_qty = stock_qty - ? WHERE id = ?
            end
        end
        
        OrderDAO->>DB: INSERT INTO orders (order_number, buyer_id, total_amount, status, shipping_address, payment_status)
        DB-->>OrderDAO: Generated orderId
        
        OrderDAO->>DB: PreparedStatement.addBatch() for order_items
        OrderDAO->>DB: executeBatch()
        
        OrderDAO->>DB: conn.commit()
        Note over OrderDAO,DB: Transaction Committed Successfully
        OrderDAO->>DB: conn.setAutoCommit(true)
        OrderDAO-->>OrderSvc: Saved Order entity
        
        OrderSvc->>CartDAO: clearCart(buyerId)
        CartDAO->>DB: DELETE FROM cart_items WHERE user_id = ?
        
        OrderSvc-->>Servlet: Placed OrderDTO (orderNumber)
        Servlet->>Buyer: Redirect /order-confirmation?orderNumber=SM-YYYYMMDD-XXXX
        
        Buyer->>Servlet: GET /order-confirmation?orderNumber=...
        Servlet->>OrderSvc: getOrderByOrderNumber(orderNumber)
        OrderSvc-->>Servlet: Optional<OrderDTO>
        Servlet->>Servlet: Verify order.buyerId == currentUser.id (Anti-IDOR)
        Servlet-->>Buyer: 200 OK Render order-confirmation.jsp
    end
```

---

## 3. Key Architectural & Reliability Guarantees

1. **ACID Transaction Compliance**: Stock deduction, order generation, and item recording occur within a single database transaction. If any item fails validation or database error occurs, `conn.rollback()` executes, leaving database records in a consistent state.
2. **Server-Side Price Calculation**: Prices are never read from user forms. Prices are looked up strictly from persistent database catalog records to prevent price tampering attacks.
3. **Cart Invalidation**: Upon successful commit, `cartDAO.clearCart(buyerId)` runs to reset the buyer's active cart state.
4. **Anti-IDOR Authorization Check**: When viewing `/order-confirmation?orderNumber=...`, the server verifies `order.getBuyerId().equals(user.getId())` to prevent buyers from snooping other customers' orders.
