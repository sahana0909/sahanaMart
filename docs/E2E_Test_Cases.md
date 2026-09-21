# End-to-End (E2E) Manual Test-Case Sheet — SahanaMart

Anna University R2025 Semester 3 Capstone Deliverable (Week 7 & 8).

| Test ID | Test Scenario | Step-by-Step Actions | Expected Output | Status |
|---------|---------------|----------------------|-----------------|--------|
| **TC-01** | User Registration (Buyer) | 1. Navigate to `/register`<br>2. Enter Name, Email, Password, Confirm Password, select Role: BUYER<br>3. Click "Create Account" | Redirects to `/login?success=...` with flash message. Account stored with BCrypt hash in DB. | **PASS** |
| **TC-02** | User Registration (Seller) | 1. Navigate to `/register`<br>2. Select Role: SELLER<br>3. Submit form | Seller account created; login succeeds and redirects to `/seller/dashboard`. | **PASS** |
| **TC-03** | Buyer Login & Session Fixation Check | 1. Navigate to `/login`<br>2. Enter credentials (`buyer@sahanamart.com` / `Buyer@123`)<br>3. Click Sign In | Old session invalidated, new session generated with 30m timeout, redirects to `/products`. | **PASS** |
| **TC-04** | Catalog Browse & Category Filter (F3) | 1. On `/products`, click category pill "Electronics"<br>2. Verify URL query `categoryId=1` | Only products matching category ID 1 are displayed. Total item count reflects filtered results. | **PASS** |
| **TC-05** | Keyword Search with Pagination (F3) | 1. Enter keyword "Headphones" in search bar<br>2. Submit search form | Sony WH-1000XM5 displayed with matching rating and price tag. | **PASS** |
| **TC-06** | Cart Add & Quantity Update (F4) | 1. On product detail page, set quantity = 2<br>2. Click "Add to Cart"<br>3. Change quantity in cart table to 3 | Subtotal dynamically recalculates: $3 \times \text{unit price}$; 5% GST recalculated; cart badge in header updates to 3. | **PASS** |
| **TC-07** | Empty Cart Checkout Edge Case | 1. Clear cart or navigate to `/checkout` directly with empty cart | Blocked with warning message: "Your cart is empty. Please add items before checking out." | **PASS** |
| **TC-08** | Atomic Checkout & Stock Deduction (F5) | 1. Add item with stock = 15 to cart (qty 2)<br>2. Proceed to `/checkout`<br>3. Input delivery address, select Mock UPI<br>4. Click "Confirm & Place Order" | Order `SM-YYYYMMDD-XXXX` generated. Inventory deducted to 13 atomically. Buyer cart cleared. Redirects to `/order-confirmation`. | **PASS** |
| **TC-09** | Order History & Workflow Status (F6, O2) | 1. Navigate to `/buyer/orders` | Placed order visible with items, amount, timestamp, and status badge `PENDING`. | **PASS** |
| **TC-10** | Seller Status Transition (O2) | 1. Sign in as `seller@sahanamart.com`<br>2. Navigate to `/seller/orders`<br>3. Update status: `PENDING` -> `CONFIRMED` -> `SHIPPED` -> `DELIVERED` | State machine transition succeeds; invalid transitions are rejected. | **PASS** |
| **TC-11** | Customer Review & Rating Submission (F8) | 1. Sign in as `buyer@sahanamart.com`<br>2. On delivered order item, click "★ Rate & Review"<br>3. Select 5 stars and enter comment<br>4. Submit review | Review saved in DB. Product average rating and review count update immediately. Duplicate submission blocked. | **PASS** |
| **TC-12** | Seller Listing Management (F2) | 1. Sign in as `seller@sahanamart.com`<br>2. Click "+ Add New Product Listing"<br>3. Enter valid title, price, stock, category<br>4. Submit | Product immediately visible in seller dashboard and buyer catalog. Low-stock indicator triggers if qty $\le 5$. | **PASS** |
| **TC-13** | Admin Listing Moderation & User Oversight (F7) | 1. Sign in as `admin@sahanamart.com`<br>2. Open `/admin/dashboard`<br>3. Click "Deactivate" on listing | Product `is_active` set to false; disappears from public buyer catalog. Global user list and order book visible. | **PASS** |
| **TC-14** | System Health API Verification (Week 8) | 1. Send HTTP GET to `/api/v1/health` | Returns HTTP 200 with exact JSON: `{"status":"UP","db":"UP"}`. | **PASS** |
| **TC-15** | Error Handling & Stack Trace Suppression | 1. Navigate to `/non-existent-page`<br>2. Trigger invalid input | Custom 404/500 JSP pages render without exposing Java stack traces or server internals. | **PASS** |
