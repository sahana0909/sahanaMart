# SahanaMart — Multi-Vendor E-Commerce Platform

[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Build-Maven%203.9-orange.svg)](https://maven.apache.org/)
[![Tomcat](https://img.shields.io/badge/Server-Apache%20Tomcat%209.0-yellow.svg)](https://tomcat.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-40%20Passed%20(100%25)-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-Academic%20Evaluation-lightgrey.svg)]()

> **Anna University R2025 Semester 3 CSE Capstone Project**  
> **Student Developer:** Sahana D (2nd Year B.E. Computer Science and Engineering)  
> **Final Capstone Review Date:** October 10, 2026  
> **Package Namespace:** `com.sahana.sahanamart`  

---

## 1. Problem Statement & Project Overview

Contemporary localized multi-vendor commerce platforms frequently suffer from brittle architectures: tight coupling between view templates and database drivers, lack of strict role isolation, inadequate query parameterization, vulnerability to session fixation, and race conditions during high-concurrency checkouts.

**SahanaMart** resolves these issues by delivering a robust, secure, and production-grade multi-vendor marketplace implemented strictly using **Java 17 Servlets, JSP/JSTL, JDBC, HikariCP, and Apache Tomcat 9**. It enforces a clean 3-tier MVC architecture (`Servlet -> Service -> DAO -> Database`), provides atomic transactional inventory deductions, features role-based access control across **Buyers**, **Sellers**, and **Platform Administrators**, and integrates an intelligent **AI & Smart FAQ Chatbot**.

---

## 2. System Architecture & Design Pattern

SahanaMart strictly adheres to the classic layered Model-View-Controller (MVC) architecture with absolute separation of concerns:

```
[ Web Browser / Client ]
       │ HTTP / HTTPS Requests
       ▼
[ Apache Tomcat 9.0.x Servlet Container ]
       ├── Filter Chain:
       │     ├── EncodingFilter (UTF-8, Security Headers: X-Content-Type-Options, DENY frame, XSS block)
       │     ├── CORSFilter (/api/* Cross-Origin Resource Sharing)
       │     └── AuthFilter (/buyer/*, /seller/*, /admin/*, /cart, /checkout, /reviews/*)
       │
       ├── Controller Layer (Java Servlets):
       │     ├── AuthServlet (/login, /register, /logout)
       │     ├── BuyerServlet (/products, /product, /search)
       │     ├── CartServlet (/cart, /cart/add, /cart/update, /cart/remove, /cart/clear)
       │     ├── CheckoutServlet (/checkout, /checkout/place-order, /order-confirmation)
       │     ├── OrderServlet (/buyer/orders, /seller/orders, /seller/orders/status)
       │     ├── ReviewServlet (/reviews/add)
       │     ├── SellerServlet (/seller/dashboard, /seller/product/*)
       │     ├── AdminServlet (/admin/dashboard, /admin/listings/moderate)
       │     ├── HealthServlet (/api/v1/health)
       │     ├── ProductApiServlet (/api/v1/products)
       │     └── ChatServlet (/api/chat)
       │
       ├── Service Layer (Business Rules & Transactions):
       │     ├── UserService (Registration validation, jBCrypt 12 rounds salting, login)
       │     ├── ProductService (Catalog search/filtering, seller ownership enforcement)
       │     ├── CartService (Server-side line item math, 5% GST calculation)
       │     ├── OrderService (Atomic stock checks, state machine transition validation)
       │     ├── ReviewService (Delivery purchase verification, duplicate blocking)
       │     └── ChatService (Sliding-window rate limiting, question caching, provider routing)
       │
       ├── DAO Layer (Data Access via Parameterized PreparedStatement):
       │     ├── UserDAO, ProductDAO, CartDAO, OrderDAO, ReviewDAO
       │     └── 100% Try-with-resources JDBC with zero SQL string concatenation
       │
       └── Persistence Layer:
             ├── HikariCP Connection Pool (Max: 15, Min Idle: 3, Timeout: 10s)
             └── H2 Persistent Storage Engine / MySQL Compatible Storage
```

---

## 3. Role-Based Capabilities

### 🛒 Buyer Features
- **User Authentication**: Secure registration and login with session fixation defense and 30-minute idle session timeout.
- **Catalog Exploration**: Real-time keyword search, category pill filters, and pagination.
- **Product Details**: High-resolution image viewing, live inventory counts, seller identification, and verified customer star ratings.
- **Shopping Cart**: Real-time quantity adjustments, item removal, running subtotal, and server-computed 5% GST.
- **Atomic Checkout & Mock Payment**: Simulated sandbox payments via Instant UPI (GPay/PhonePe), Credit/Debit Card (3D Secure), or Cash on Delivery (COD).
- **Order Tracking & History**: Full order timeline tracking across `PENDING` -> `CONFIRMED` -> `SHIPPED` -> `DELIVERED`.
- **Verified Customer Reviews**: 1 to 5-star rating submission enabled strictly on delivered orders with anti-duplicate review protections.
- **Interactive AI Assistant**: Embedded floating chatbot widget available across all pages for instant shopping inquiries.

### 🏪 Seller Features
- **Dedicated Seller Dashboard**: Visual inventory table displaying product status, unit prices, available stock, and average star ratings.
- **Listing Management**: Add, update, and delete listings with mandatory category assignment and image URLs.
- **Low-Stock Alerts**: Automatic visual badge warnings whenever inventory dips to 5 units or lower.
- **Cross-Seller Ownership Protection**: Rigorous server-side verification ensures sellers cannot update or delete listings belonging to other merchants.
- **Incoming Orders Book**: Review orders containing seller products, inspect shipping addresses, and advance order workflow statuses.

### 🛡️ Administrator Features
- **Admin Panel**: High-level platform metrics: total registered users, active catalog listings, and gross orders placed.
- **User Registry**: Audit registered buyers and sellers with timestamps and contact details.
- **Catalog Moderation**: One-click toggling to instantly activate or deactivate inappropriate listings.
- **No Public Registration**: Admin accounts cannot be self-registered from public web forms; seeded securely upon system initialization.

---

## 4. Mandatory AI & Smart FAQ Chatbot (CapStone Feature)

The AI Chatbot is designed as a hybrid assistant that functions seamlessly offline while optionally leveraging external AI:

- **Architecture**:
  - `ChatProvider` interface defining the conversational contract.
  - `MockChatProvider`: Embedded rule-based smart knowledge engine with 10+ FAQ domains (order tracking, payment methods, delivery timelines, return policies, seller registration, customer reviews, tech stack, and polite out-of-scope redirection).
  - `GeminiChatProvider`: External integration with Google Gemini (`gemini-1.5-flash`) REST API via standard Java `HttpClient`. Enforces 5-second timeouts and automatically delegates to `MockChatProvider` if an API key is absent, expired, or network-constrained.
  - `ChatService`: Manages sliding-window rate limiting (**10 messages per minute per session**), message length validation (1–500 characters), and per-session identical question caching.
  - `ChatServlet`: REST endpoint at `POST /api/chat`.
- **Frontend Widget**:
  - Collapsible floating widget in `common/chatbot.jsp` included globally.
  - Pre-populated clickable suggestion chips for instant 1-click answers.
  - XSS-safe text rendering via `textContent`.
  - Typing indicator animation and smooth auto-scrolling.

---

## 5. Technology Stack

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java Development Kit (JDK) | 17 LTS | Core application backend |
| **Web Container** | Apache Tomcat | 9.0.86 | Servlet 4.0 & JSP 2.3 container |
| **Build & Dependencies** | Apache Maven | 3.9+ | Build lifecycle, dependencies, packaging |
| **Presentation Tier** | JSP & JSTL | 1.2 | Server-rendered templates with XSS escaping |
| **Connection Pooling** | HikariCP | 5.1.0 | High-performance JDBC connection pool |
| **Persistence Engine** | H2 Database / MySQL | 2.2.224 | ACID relational storage with file persistence |
| **Security & Hashing** | jBCrypt | 0.4 | Salted slow hashing (12 log rounds) |
| **JSON Serialization** | Jackson Databind | 2.16.1 | REST API & Chatbot JSON processing |
| **Automated Testing** | JUnit 5 & Mockito | 5.10.2 / 5.11.0 | Unit, DAO, Service, and Controller test suites |
| **Styling & UI** | CSS3 & HTML5 | Standard | Responsive corporate Blue, Black, and White theme |

---

## 6. Pre-Seeded Evaluation Accounts

The database seeds automatically upon initial application startup with the following demonstration credentials:

| Role | Email Address | Password | Permissions & Dashboard |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@sahanamart.com` | `Admin@123` | Platform oversight, product moderation, global orders |
| **Seller** | `seller@sahanamart.com` | `Seller@123` | Listing creation/editing, inventory alerts, incoming orders |
| **Buyer** | `buyer@sahanamart.com` | `Buyer@123` | Browsing, shopping cart, checkout, order history, reviews |

> *Tip: The login page includes 1-click credential helper buttons to instantly populate these demo accounts.*

---

## 7. Build, Test, and Execution Instructions

### Prerequisites
- JDK 17 or higher installed (`java -version`).
- Apache Maven installed (`mvn -version`).

### Build & Run Automated Tests
Execute the Maven verification lifecycle:
```bash
mvn clean verify
```
*Expected Result:*
- All **40 unit and integration tests** pass cleanly (0 failures, 0 errors).
- SpotBugs and Checkstyle inspections pass.
- WAR package generated at `target/sahanamart.war`.

### Running Locally with Embedded Tomcat Runner
No separate Tomcat installation is required. Run the embedded launcher:
```bash
# Option A: Windows Batch Script
run.bat

# Option B: Direct Maven Command
mvn test-compile exec:java
```

Once started:
- **Storefront Home:** [http://localhost:8080/sahanamart/](http://localhost:8080/sahanamart/)
- **Product Catalog:** [http://localhost:8080/sahanamart/products](http://localhost:8080/sahanamart/products)
- **Sign In Page:** [http://localhost:8080/sahanamart/login](http://localhost:8080/sahanamart/login)
- **Health Check API:** [http://localhost:8080/sahanamart/api/v1/health](http://localhost:8080/sahanamart/api/v1/health)

---

## 8. Security Audit & Hardening Matrix

| Security Parameter | Defense Mechanism in SahanaMart | Verification Result |
| :--- | :--- | :---: |
| **SQL Injection Prevention** | 100% of queries use `PreparedStatement` with parameterized `?` placeholders. Zero string concatenation. | **PASS** |
| **Password Hashing** | Passwords hashed with `jBCrypt` (12 rounds salting). Plaintext is never stored, logged, or serialized. | **PASS** |
| **Session Fixation Defense** | Existing session is explicitly invalidated (`session.invalidate()`) and a fresh session ID is issued upon login. | **PASS** |
| **Session Inactivity Timeout** | Explicit 30-minute session timeout enforced via `<session-timeout>30</session-timeout>` in `web.xml`. | **PASS** |
| **IDOR Defense** | Order confirmation and detail endpoints verify `order.buyerId == currentUser.id` before rendering. | **PASS** |
| **Cross-Site Scripting (XSS)** | Dynamic attributes escaped via JSTL `<c:out value="..."/>` and `fn:escapeXml`. Chatbot uses `textContent`. | **PASS** |
| **Cross-Seller Tampering** | `ProductService` checks `existingProduct.sellerId == requesterId` before allowing edit/delete operations. | **PASS** |
| **Atomic Inventory Deduction** | Database transactions (`setAutoCommit(false)`, `commit()`, `rollback()`) guarantee ACID checkout integrity. | **PASS** |
| **HTTP Security Headers** | `EncodingFilter` attaches `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `X-XSS-Protection: 1; mode=block`. | **PASS** |
| **Stack Trace Suppression** | Custom `404.jsp` and `500.jsp` pages mapped in `web.xml` suppress internal stack traces and server details. | **PASS** |
| **Secret Management** | Credentials and API keys excluded from version control via `.gitignore`. `.env.example` provided. | **PASS** |

---

## 9. Capstone Review Deliverables & Documentation Directory

- **D1 Entity-Relationship Diagram:** [`docs/D1_ER_Diagram.md`](docs/D1_ER_Diagram.md)
- **D2 Use Case Diagram (Buyer, Seller, Admin, AI):** [`docs/D2_UseCase_Diagram.md`](docs/D2_UseCase_Diagram.md)
- **D3 Place Order Sequence Diagram:** [`docs/D3_Sequence_Diagram.md`](docs/D3_Sequence_Diagram.md)
- **End-to-End Test Case Sheet (TC-01 to TC-15):** [`docs/E2E_Test_Cases.md`](docs/E2E_Test_Cases.md)
- **Security Audit Checklist:** [`docs/Security_Checklist.md`](docs/Security_Checklist.md)
- **Sprint Retrospective Log (Weeks 1 to 9 & Final):** [`RETRO.md`](RETRO.md)
- **Final Capstone Project Evaluation Report:** [`docs/Final_Capstone_Report.md`](docs/Final_Capstone_Report.md)

---

## 10. Student Declaration

I hereby declare that the capstone project titled **"SahanaMart — A Multi-Vendor E-Commerce Platform with Layered Java Servlet Architecture and AI Assistance"** submitted for the Anna University R2025 Semester 3 Computer Science and Engineering evaluation is my original work completed under the prescribed curriculum guidelines.

**Student:** Sahana D  
**Department:** Computer Science and Engineering  
**Academic Year:** 2026–2027  
**Submission Date:** October 10, 2026
