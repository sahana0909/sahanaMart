# D2: System Use Case Diagram — SahanaMart

Anna University R2025 Semester 3 Capstone Deliverable (Kickoff & Final Review).

## Actors
- **Buyer**: Registered consumer purchasing products, managing cart, viewing order history, leaving reviews.
- **Seller**: Merchant managing product listings, monitoring stock levels, and updating incoming order statuses.
- **Admin**: Platform administrator moderating listings, reviewing users, and viewing global transactions.
- **Guest / Visitor**: Unauthenticated user browsing catalog and signing up.

```mermaid
flowchart LR
    subgraph Users
        G["Guest / Visitor"]
        B["Buyer"]
        S["Seller"]
        A["Admin"]
    end

    subgraph "Authentication (F1)"
        UC1["Register Account (Buyer/Seller)"]
        UC2["Login & Session Invalidation"]
        UC3["Logout"]
    end

    subgraph "Catalog & Browsing (F3)"
        UC4["Browse Products with Category Filter"]
        UC5["Keyword Search with Pagination"]
        UC6["View Product Detail & Customer Reviews"]
    end

    subgraph "Cart & Checkout Flow (F4, F5)"
        UC7["Add / Update / Remove Cart Items"]
        UC8["View Running Subtotal & Tax"]
        UC9["Mock Payment Confirmation & Place Order"]
    end

    subgraph "Orders & Reviews (F6, F8, O2)"
        UC10["View Buyer Order History"]
        UC11["Submit Star Rating & Review (Delivered Only)"]
        UC12["Seller View Incoming Orders"]
        UC13["Update Workflow Status (Pending->Confirmed->Shipped->Delivered)"]
    end

    subgraph "Seller & Admin Management (F2, F7)"
        UC14["Seller Create / Edit / Delete Listings"]
        UC15["Seller Monitor Inventory & Low Stock"]
        UC16["Admin View All Registered Users"]
        UC17["Admin Moderate / Deactivate Product Listings"]
        UC18["Admin Audit System Orders"]
    end

    G --> UC1
    G --> UC2
    G --> UC4
    G --> UC5
    G --> UC6

    B --> UC2
    B --> UC3
    B --> UC4
    B --> UC5
    B --> UC6
    B --> UC7
    B --> UC8
    B --> UC9
    B --> UC10
    B --> UC11

    S --> UC2
    S --> UC3
    S --> UC12
    S --> UC13
    S --> UC14
    S --> UC15

    A --> UC2
    A --> UC3
    A --> UC16
    A --> UC17
    A --> UC18
```
