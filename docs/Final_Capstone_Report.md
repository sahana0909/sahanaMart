# SahanaMart: Multi-Vendor E-Commerce Platform
## Capstone Project Final Review & Technical Report

**Anna University R2025 Regulations — Semester 3**  
**Department of Computer Science and Engineering**  

- **Project Title:** SahanaMart — Layered Java Web Application with Role-Based Access Control and AI Assistance
- **Student Name:** Sahana D
- **Academic Level:** 2nd Year B.E. Computer Science and Engineering
- **Package Architecture:** `com.sahana.sahanamart`
- **Evaluation Date:** October 10, 2026
- **Status:** Complete, Tested, Hardened, and Ready for Defense

---

## 1. Executive Summary

Electronic commerce platforms serving regional and multi-vendor marketplaces require high operational resilience, strict transactional guarantees, rigorous data privacy, and role isolation. Modern enterprise systems rely heavily on heavyweight application frameworks (such as Spring Boot), which often abstract underlying HTTP, servlet lifecycle, and database connection pooling mechanisms away from engineering students.

The **SahanaMart** project demonstrates that an enterprise-grade, multi-vendor marketplace can be built from fundamental web engineering principles using **Java 17, Java Servlets 4.0, JSP/JSTL 1.2, JDBC, and Apache Tomcat 9.0**.

By adhering strictly to a 3-tier MVC architecture (`Servlet Dispatcher -> Service Layer -> Data Access Object Layer -> Relational Persistent Storage`), SahanaMart establishes:
1. **100% Parameterized Query Execution** with zero string concatenation.
2. **Atomic Transactional Checkout** preventing overselling and inventory race conditions.
3. **Multi-Role Security** covering Buyers, independent Sellers, and platform Administrators.
4. **Hybrid AI Assistance** through an integrated Smart FAQ and Google Gemini AI chatbot.
5. **Zero Stack Trace Exposure** and comprehensive OWASP Top 10 mitigation.

---

## 2. System Requirements Specification (SRS)

### 2.1 Functional Requirements (FR)

| Req ID | Title | Description | Implementation Module |
| :--- | :--- | :--- | :--- |
| **FR-01** | Account Registration & Roles | Users can register as BUYER or SELLER. Admin role is pre-seeded and cannot be self-registered. | `AuthServlet`, `UserServiceImpl`, `UserDAOImpl` |
| **FR-02** | Secure Authentication | Login with salted BCrypt password verification, session regeneration to stop session fixation, and 30-min idle timeout. | `AuthServlet`, `PasswordUtil`, `AuthFilter` |
| **FR-03** | Catalog Browsing & Search | Real-time keyword search, category pill navigation, and pagination of active products. | `BuyerServlet`, `ProductServiceImpl`, `ProductDAOImpl` |
| **FR-04** | Cart Management | Add to cart, modify quantities, remove line items, with server-side 5% GST computation. | `CartServlet`, `CartServiceImpl`, `CartDAOImpl` |
| **FR-05** | Atomic Checkout | Simulated payment options (UPI, Card, COD) with atomic stock deduction in a database transaction. | `CheckoutServlet`, `OrderServiceImpl`, `OrderDAOImpl` |
| **FR-06** | Order Status Workflow | Transition orders across PENDING -> CONFIRMED -> SHIPPED -> DELIVERED via constrained state machine. | `OrderServlet`, `OrderStatus`, `OrderServiceImpl` |
| **FR-07** | Verified Customer Reviews | 1 to 5-star ratings and written reviews allowed strictly for buyers who completed delivered orders. | `ReviewServlet`, `ReviewServiceImpl`, `ReviewDAOImpl` |
| **FR-08** | Seller Management | Sellers can create, edit, and delete their own listings with inventory alerts ($\le 5$ units). Cross-seller tampering blocked. | `SellerServlet`, `ProductServiceImpl` |
| **FR-09** | Platform Moderation | Administrators can view all users, global orders, and deactivate/activate listings in real time. | `AdminServlet`, `UserServiceImpl`, `ProductServiceImpl` |
| **FR-10** | AI Shopping Assistant | Embedded chatbot providing 24/7 answers to e-commerce FAQs and Gemini AI integration with rate limiting. | `ChatServlet`, `ChatService`, `MockChatProvider`, `GeminiChatProvider` |

### 2.2 Non-Functional Requirements (NFR)

1. **Performance**: System responds to catalog and health check requests in $< 80\text{ ms}$ under local execution. Connection pooling managed by HikariCP maintains $\le 15$ active connections with minimal acquisition latency.
2. **Reliability & ACID**: Checkout operations guarantee Atomicity, Consistency, Isolation, and Durability using JDBC connection-level transactions (`setAutoCommit(false)`, `commit()`, `rollback()`).
3. **Security**: Zero plaintext passwords; passwords salted with `jBCrypt` (12 rounds). Session cookies set to `HttpOnly`. Response headers attach `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`.
4. **Maintainability**: Layered separation of concerns. Servlets contain no business logic or SQL; JSPs contain no Java scriptlets (`<% %>`), using standard JSTL `<c:out>` instead.
5. **Portability**: Packaged as a standard portable WAR file compliant with Servlet 4.0; runs on Apache Tomcat 9 standalone or embedded via `ServerRunner.java`.

---

## 3. Architecture & Software Design

### 3.1 3-Tier Layered Architecture

```
Layer 1: Presentation Tier
  ├── JSP Views (Buyer, Seller, Admin, Common, Error)
  ├── JSTL Expression Language & Tag Libraries (c:out, fn:escapeXml)
  └── Static Assets (style.css, app.js, Fetch API)
         │ HTTP / JSON
         ▼
Layer 2: Controller & Dispatcher Tier
  ├── Filters (EncodingFilter, CORSFilter, AuthFilter)
  └── Java Servlets (AuthServlet, BuyerServlet, CartServlet, CheckoutServlet, 
                     OrderServlet, ReviewServlet, SellerServlet, AdminServlet, 
                     HealthServlet, ProductApiServlet, ChatServlet)
         │ Service Method Calls
         ▼
Layer 3: Business Logic & Domain Tier
  ├── Services (UserService, ProductService, CartService, OrderService, ReviewService, ChatService)
  └── Domain Models & DTO Envelopes (User, Product, Order, OrderItem, CartItem, Review)
         │ Parameterized DAO Invocations
         ▼
Layer 4: Data Access & Persistence Tier
  ├── DAOs (UserDAO, ProductDAO, CartDAO, OrderDAO, ReviewDAO)
  ├── DatabaseUtil (HikariDataSource pooling, Script Runner)
  └── Relational DB Engine (H2 persistent storage / MySQL compatible)
```

### 3.2 Design Patterns Employed

- **Model-View-Controller (MVC)**: Decouples user interface rendering from domain logic and storage.
- **Data Access Object (DAO) Pattern**: Encapsulates all relational database interaction behind typed interfaces.
- **Service Layer Pattern**: Isolates transaction boundaries and business validation rules.
- **Strategy / Provider Pattern**: Encapsulates Chatbot logic into interchangeable implementations (`ChatProvider`, `MockChatProvider`, `GeminiChatProvider`).
- **Data Transfer Object (DTO) Pattern**: Decouples persistent relational models from HTTP responses, preventing password hash leakage (`UserResponseDTO`).
- **State Machine Pattern**: Enforces valid lifecycle transitions on `OrderStatus` enum.

---

## 4. AI Chatbot Technical Implementation

The SahanaMart Chatbot fulfills the mandatory Anna University capstone requirement for an intelligent customer-facing assistant:

```
[ User Prompt via Widget ]
          │ POST /api/chat (JSON)
          ▼
[ ChatServlet Controller ]
          │ Extract Session ID & Message
          ▼
[ ChatService Orchestrator ]
  ├── 1. Length & Empty Validation (1–500 chars)
  ├── 2. Sliding Rate Limiter (Max 10 req/min per session)
  ├── 3. Session Cache Check (Instant return if identical question answered)
  └── 4. Provider Delegation
            ├── Try: GeminiChatProvider (Google Gemini API via HttpClient, 5s timeout)
            └── Catch / Fallback: MockChatProvider (Intelligent Keyword & Rule Matching)
```

### Knowledge Base Domains in Mock Provider
1. Order Tracking & Lifecycle (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`).
2. Payment Methods (Simulated UPI, Card, Cash on Delivery).
3. 7-Day Returns & Refund Guarantees.
4. Seller Registration & Store Dashboard Operations.
5. Product Catalog & Category Taxonomy.
6. Customer Star Ratings & Verified Purchase Review Verification.
7. Technical Architecture & Capstone Provenance.
8. Polite Guardrails declining out-of-scope queries.

---

## 5. Security & Threat Modeling Audit

| Threat / Attack Vector | Mitigation in SahanaMart | Verification Evidence |
| :--- | :--- | :--- |
| **SQL Injection (SQLi)** | 100% of SQL statements constructed with `PreparedStatement` and typed setters (`setString`, `setLong`, `setBigDecimal`). | Verified via static search across all `dao/impl/*` classes. |
| **Broken Authentication** | Passwords stored with salted `jBCrypt` (12 rounds). Failed logins return generic error messages. | Tested in `UserServiceTest.java`. |
| **Session Fixation** | Old session invalidated (`session.invalidate()`) on login; fresh session generated with `HttpOnly` cookies. | Verified in `AuthServlet.java`. |
| **Insecure Direct Object Reference (IDOR)** | `/order-confirmation` and review forms verify that requested order belongs to current user or administrator. | Verified in `CheckoutServlet.java` & `ReviewServiceImpl.java`. |
| **Cross-Site Scripting (XSS)** | All user data rendered using `<c:out value="..."/>` or JS `textContent`. | Injection payloads safely escaped as plain text. |
| **Cross-Seller Tampering** | Seller IDs matched against product owner on every update or delete operation on the server. | Verified in `ProductServiceImpl.java`. |
| **Chatbot Abuse / DoS** | Sliding-window session rate limiter enforces max 10 requests per minute with HTTP 429 response. | Tested in `ChatServiceTest.java`. |
| **Information Disclosure** | Custom 404/500 error pages mapped in `web.xml`; server stack traces suppressed from HTTP responses. | Tested via invalid URL navigation. |

---

## 6. Verification & Test Results

### 6.1 Automated Unit & Integration Tests

The test suite executed via Maven Surefire:
- **Test Count:** 40 Tests Executed
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0
- **Build Status:** **BUILD SUCCESS (100% Green)**

```
Test Classes Executed:
├── com.sahana.sahanamart.dao.CartDAOTest (2 tests) - PASSED
├── com.sahana.sahanamart.dao.OrderDAOTest (1 test) - PASSED
├── com.sahana.sahanamart.dao.ProductDAOTest (3 tests) - PASSED
├── com.sahana.sahanamart.dao.UserDAOTest (2 tests) - PASSED
├── com.sahana.sahanamart.service.CartServiceTest (2 tests) - PASSED
├── com.sahana.sahanamart.service.ChatServiceTest (5 tests) - PASSED
├── com.sahana.sahanamart.service.MockChatProviderTest (7 tests) - PASSED
├── com.sahana.sahanamart.service.OrderServiceTest (3 tests) - PASSED
├── com.sahana.sahanamart.service.ProductServiceTest (3 tests) - PASSED
├── com.sahana.sahanamart.service.ReviewServiceTest (5 tests) - PASSED
├── com.sahana.sahanamart.service.UserServiceTest (4 tests) - PASSED
└── com.sahana.sahanamart.controller.ChatServletTest (3 tests) - PASSED
```

### 6.2 Manual End-to-End Test Matrix

All 15 manual test scenarios documented in [`docs/E2E_Test_Cases.md`](E2E_Test_Cases.md) were executed against the live application:
- **TC-01 to TC-03 (Authentication & RBAC):** PASS
- **TC-04 to TC-05 (Catalog Browsing & Search):** PASS
- **TC-06 to TC-07 (Cart Operations & Empty Cart Guard):** PASS
- **TC-08 (Atomic Checkout & Stock Reservation):** PASS
- **TC-09 to TC-10 (Order Tracking & Status Workflow):** PASS
- **TC-11 (Verified Purchase Customer Reviews):** PASS
- **TC-12 to TC-13 (Seller Dashboard & Admin Panel):** PASS
- **TC-14 (REST Health Check Endpoint):** PASS (`{"status":"UP","db":"UP"}`)
- **TC-15 (Custom Error Handling & Stack Trace Suppression):** PASS

---

## 7. Capstone Demonstration Walkthrough Script

For the final viva-voce and evaluation presentation, follow this 5-minute demonstration script:

1. **Architecture & Startup (1 minute)**:
   - Run `run.bat` or `mvn test-compile exec:java`.
   - Show server launch logs confirming HikariCP connection pool initialization and database schema creation.
   - Open [http://localhost:8080/sahanamart/](http://localhost:8080/sahanamart/) in browser.
   - Access [http://localhost:8080/sahanamart/api/v1/health](http://localhost:8080/sahanamart/api/v1/health) to show live JSON health state.
2. **Buyer Shopping & Atomic Checkout Journey (1.5 minutes)**:
   - Click "Sign In", click 1-click button for **Buyer** (`buyer@sahanamart.com`).
   - Browse catalog, search for "Headphones", click into product details.
   - Add item to cart, inspect 5% GST computation, and proceed to checkout.
   - Select "Simulated UPI", input delivery address, and click "Confirm & Place Order".
   - Show generated Order Number (`SM-YYYYMMDD-XXXX`) and order confirmation.
   - Check "My Orders" to show status `PENDING`.
3. **Seller Order Processing & Inventory Flow (1 minute)**:
   - Logout and sign in as **Seller** (`seller@sahanamart.com`).
   - Show Seller Dashboard displaying owned products and low-stock indicators.
   - Open "Incoming Orders" and advance the newly placed order: `PENDING` -> `CONFIRMED` -> `SHIPPED` -> `DELIVERED`.
4. **Verified Customer Review (45 seconds)**:
   - Sign back in as **Buyer**, navigate to "My Orders".
   - On the `DELIVERED` order item, click "★ Rate & Review".
   - Submit a 5-star rating and comment.
   - Verify that the average rating updates immediately and duplicate review submissions are prevented.
5. **AI Chatbot & Security Controls (45 seconds)**:
   - Open the floating chatbot bubble on bottom-right.
   - Click suggestion chip "📦 Track order" to show instant FAQ response.
   - Type "What payment methods are supported?" to show payment answer.
   - Rapidly send messages to show the sliding rate limiter in action.
   - Sign in as **Admin** (`admin@sahanamart.com`) and show platform metrics, user registry, and listing moderation.

---

## 8. Conclusion

The **SahanaMart** capstone project successfully demonstrates an enterprise-grade, multi-vendor online marketplace engineered strictly according to Anna University R2025 Semester 3 Computer Science specifications. It bridges theoretical principles of database design, operating system concurrency, network security, and software engineering with a practical, polished web application ready for deployment and defense.

**Submitted By:** Sahana D  
**Department:** Computer Science and Engineering  
**Anna University R2025 Capstone Evaluation — October 10, 2026**
