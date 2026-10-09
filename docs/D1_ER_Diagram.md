# D1: Entity-Relationship (ER) Diagram — SahanaMart

Anna University R2025 Semester 3 CSE Capstone Deliverable.  
**Student:** Sahana D  
**System:** SahanaMart Multi-Vendor E-Commerce Platform  

---

## 1. Relational Database Model Overview

The SahanaMart persistence tier is designed to support multi-vendor e-commerce with strict relational integrity, cascade protections, and index optimization. The core entity relationships are:

- **USERS** $\rightarrow$ **PRODUCTS**: One Seller owns many Products ($1:N$).
- **CATEGORIES** $\rightarrow$ **PRODUCTS**: One Category organizes many Products ($1:N$).
- **USERS** $\rightarrow$ **CART_ITEMS**: One User holds many Cart Items ($1:N$).
- **PRODUCTS** $\rightarrow$ **CART_ITEMS**: One Product can appear in multiple shopping carts ($1:N$).
- **USERS** $\rightarrow$ **ORDERS**: One Buyer places many Orders ($1:N$).
- **ORDERS** $\rightarrow$ **ORDER_ITEMS**: One Order contains multiple line items ($1:N$).
- **PRODUCTS** $\rightarrow$ **ORDER_ITEMS**: An Order Item references a specific snapshot of a Product ($1:N$).
- **PRODUCTS** $\rightarrow$ **REVIEWS**: One Product receives many customer reviews ($1:N$).
- **USERS** $\rightarrow$ **REVIEWS**: One Buyer writes verified reviews ($1:N$).
- **ORDERS** $\rightarrow$ **REVIEWS**: One delivered Order is linked to a Review to ensure verified purchase status ($1:1$ per product).

---

## 2. Mermaid Entity-Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "sells (1:N)"
    USERS ||--o{ CART_ITEMS : "has (1:N)"
    USERS ||--o{ ORDERS : "places (1:N)"
    USERS ||--o{ REVIEWS : "writes (1:N)"
    
    CATEGORIES ||--o{ PRODUCTS : "categorizes (1:N)"
    
    PRODUCTS ||--o{ CART_ITEMS : "added to (1:N)"
    PRODUCTS ||--o{ ORDER_ITEMS : "contained in (1:N)"
    PRODUCTS ||--o{ REVIEWS : "evaluated in (1:N)"
    
    ORDERS ||--|{ ORDER_ITEMS : "consists of (1:N)"
    ORDERS ||--o{ REVIEWS : "verifies delivery of (1:N)"

    USERS {
        bigint id PK "Auto Increment"
        varchar email UK "Unique, lowercased"
        varchar password_hash "Salted BCrypt hash (12 rounds)"
        varchar full_name "User display name"
        varchar role "BUYER, SELLER, ADMIN"
        varchar phone "Contact number"
        timestamp created_at "Registration timestamp"
    }

    CATEGORIES {
        bigint id PK "Auto Increment"
        varchar name UK "Unique category title"
        varchar description "Category overview"
        timestamp created_at "Creation timestamp"
    }

    PRODUCTS {
        bigint id PK "Auto Increment"
        bigint seller_id FK "References USERS(id)"
        bigint category_id FK "References CATEGORIES(id)"
        varchar name "Product title"
        clob description "Product specifications"
        decimal price "Unit price in INR"
        int stock_qty "Available physical stock"
        varchar image_url "Secure HTTPS image CDN"
        boolean is_active "Moderation/visibility state"
        timestamp created_at "Listing timestamp"
    }

    CART_ITEMS {
        bigint id PK "Auto Increment"
        bigint user_id FK "References USERS(id)"
        bigint product_id FK "References PRODUCTS(id)"
        int quantity "Requested units"
        timestamp created_at "Added timestamp"
    }

    ORDERS {
        bigint id PK "Auto Increment"
        varchar order_number UK "SM-YYYYMMDD-XXXX"
        bigint buyer_id FK "References USERS(id)"
        decimal total_amount "Order total with 5% GST"
        varchar status "PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED"
        varchar shipping_address "Destination address text"
        varchar payment_status "COMPLETED (Mock payment sandbox)"
        varchar tracking_number "Courier tracking code"
        timestamp created_at "Order placement timestamp"
    }

    ORDER_ITEMS {
        bigint id PK "Auto Increment"
        bigint order_id FK "References ORDERS(id)"
        bigint product_id FK "References PRODUCTS(id)"
        bigint seller_id FK "References USERS(id)"
        int quantity "Purchased count"
        decimal unit_price "Historical price snapshot"
        decimal total_price "quantity * unit_price"
        timestamp created_at "Creation timestamp"
    }

    REVIEWS {
        bigint id PK "Auto Increment"
        bigint product_id FK "References PRODUCTS(id)"
        bigint buyer_id FK "References USERS(id)"
        bigint order_id FK "References ORDERS(id)"
        int rating "1 to 5 integer check"
        clob comment "Customer feedback text"
        timestamp created_at "Submission timestamp"
    }
```

---

## 3. Schema Constraints & Data Integrity

1. **Composite Uniqueness**:
   - `CART_ITEMS`: `uk_cart_user_product UNIQUE (user_id, product_id)` ensures a buyer cannot duplicate rows; quantity is updated instead.
   - `REVIEWS`: `uk_reviews_buyer_order_product UNIQUE (buyer_id, order_id, product_id)` restricts each buyer to exactly one verified review per purchased delivered order item.
2. **Referential Cascade & Delete Rules**:
   - Deleting a category is restricted (`ON DELETE RESTRICT`) while products exist.
   - Deleting a user cascades to cart items (`ON DELETE CASCADE`), but preserves order history through restricted foreign keys (`ON DELETE RESTRICT`) to protect accounting integrity.
3. **Optimized Indexes**:
   - `idx_products_seller` on `products(seller_id)`
   - `idx_products_category` on `products(category_id)`
   - `idx_cart_user` on `cart_items(user_id)`
   - `idx_orders_buyer` on `orders(buyer_id)`
   - `idx_order_items_order` on `order_items(order_id)`
   - `idx_reviews_product` on `reviews(product_id)`
