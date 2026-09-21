# SahanaMart MVP Review Summary (Aug 10 Checkpoint)

Anna University R2025 Semester 3 Capstone Deliverable.

---

## Slide 1: Problem Statement & Architectural Blueprint

### Problem Statement
Traditional localized online commerce systems often suffer from coupled architectures, lack of strict role isolation, inadequate query parameterization, and fragile transaction models. **SahanaMart** provides a robust, highly modular e-commerce platform built strictly on Java 17 Servlets, JDBC, and Apache Tomcat 9, featuring a reliable separation of concerns between Controllers, Services, and Data Access Objects (DAOs).

### Architectural Overview
- **Presentation Layer**: JSP views rendering clean HTML, fortified with JSTL `<c:out>` and `fn:escapeXml` XSS escaping.
- **Controller / Dispatch**: Java Servlets (`AuthServlet`, `BuyerServlet`, `CartServlet`, `CheckoutServlet`, `OrderServlet`, `SellerServlet`, `AdminServlet`, `HealthServlet`).
- **Security & Interception**:
  - `EncodingFilter`: Enforces UTF-8 and security response headers (CSP, XSS, nosniff, DENY frame).
  - `AuthFilter`: Session validation, role-based access enforcement for `/buyer/*`, `/seller/*`, `/admin/*`.
- **Service Layer**: Business rule validation, password hashing via jBCrypt, atomic checkout logic.
- **DAO Layer**: 100% parameterized `PreparedStatement` with try-with-resources. Zero SQL concatenation.
- **Persistence & Connection Pool**: H2 database running with persistent file storage and HikariCP connection pooling managed by `AppContextListener`.

```
[ Browser / Client ]
        │ HTTP / HTTPS
        ▼
[ Servlet Container (Tomcat 9.0.x) ]
   ├── Filters: EncodingFilter -> AuthFilter -> CORSFilter
   ├── Controllers: Servlets (/products, /cart, /checkout, /api/v1/health)
   ├── Service Layer: UserService, ProductService, CartService, OrderService
   ├── DAO Layer: UserDAO, ProductDAO, CartDAO, OrderDAO (PreparedStatement)
   └── Storage: HikariCP Connection Pool ──► H2 Persistent Storage Engine
```

---

## Slide 2: Completed MVP vs. Planned Roadmap

### Completed Features (MVP Review Baseline)
| Feature ID | Scope Description | Status | Verification |
|------------|-------------------|--------|--------------|
| **F1** | Authentication & RBAC (Buyer, Seller, Seeded Admin) | **Done** | jBCrypt hashing, session regeneration |
| **F2** | Seller listing management (Create, Edit, Delete) | **Done** | Category picker, stock validation |
| **F3** | Buyer browse and search/filter catalog | **Done** | Keyword search, category pills, pagination |
| **F4** | Cart operations (Add, update quantity, remove, total) | **Done** | Running totals, inventory checks |
| **F5** | Checkout with mock payment confirmation | **Done** | Stock reservation, atomic transaction |
| **Infra** | DB schema v1 & seed data | **Done** | H2 file persistence, HikariCP pool |
| **CI** | Automated build & JUnit 5 tests | **Done** | 100% tests green (`mvn clean verify`) |

### Completed End-to-End User Journey
**Buyer Flow**:
1. User registers new buyer account at `/register`.
2. Signs in at `/login` (session regenerated).
3. Browses products at `/products`, filters by "Electronics", searches keyword.
4. Adds item to cart (`/cart/add`), verifies running subtotal & 5% GST at `/cart`.
5. Enters delivery address and confirms payment at `/checkout`.
6. Order confirmed (`SM-YYYYMMDD-XXXX`) with inventory deducted atomically.

### Planned Roadmap (Weeks 3–8 Full Build)
- Week 3–4: Seller dashboard refinement, Admin moderation panel (F7), Buyer & Seller order views (F6).
- Week 5: Search refinement & order status workflow (O2: Pending -> Confirmed -> Shipped -> Delivered).
- Week 6: Product reviews & star ratings on completed orders (F8).
- Week 7: Security checklist audit, 100% PreparedStatement verification, JMeter/ab load test.
- Week 8: Deploy link review, `/api/v1/health` verification, release tag `v1.0.0`.
