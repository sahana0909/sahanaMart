<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Secure Checkout | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 26px; font-weight: 800; margin-bottom: 24px;">Complete Your Order</h1>

<form action="${pageContext.request.contextPath}/checkout/place-order" method="post">
    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 30px;">
        <div>
            <!-- Shipping Address Form -->
            <div class="card">
                <h3 style="font-size: 18px; font-weight: 800; margin-bottom: 16px;">1. Shipping & Delivery Address</h3>
                <div class="form-group">
                    <label for="shippingAddress">Full Delivery Address (Street, Apartment, City, State, PIN)</label>
                    <textarea id="shippingAddress" name="shippingAddress" rows="3" class="form-control" required
                              placeholder="e.g. Flat 304, Royal Towers, GST Road, Guindy, Chennai, Tamil Nadu - 600032"><c:out value="${shippingAddress != null ? shippingAddress : 'Flat 304, Royal Towers, GST Road, Guindy, Chennai, Tamil Nadu - 600032'}"/></textarea>
                </div>
            </div>

            <!-- Mock Payment Confirmation Step (F5) -->
            <div class="card">
                <h3 style="font-size: 18px; font-weight: 800; margin-bottom: 8px;">2. Payment Method (F5 Mock Confirmation)</h3>
                <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 16px;">
                    This is a simulated sandbox payment step for academic capstone demonstration. No actual currency is charged.
                </p>

                <div style="display: flex; flex-direction: column; gap: 10px;">
                    <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--primary-light); border-radius: var(--radius); cursor: pointer; background: var(--gray-50);">
                        <input type="radio" name="paymentMethod" value="MOCK_UPI" checked />
                        <div>
                            <strong>Simulated UPI (Google Pay / PhonePe)</strong>
                            <div style="font-size: 12px; color: var(--gray-500);">Instant mock payment approval</div>
                        </div>
                    </label>

                    <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius); cursor: pointer;">
                        <input type="radio" name="paymentMethod" value="MOCK_CARD" />
                        <div>
                            <strong>Simulated Credit / Debit Card</strong>
                            <div style="font-size: 12px; color: var(--gray-500);">Instant mock 3D Secure simulation</div>
                        </div>
                    </label>

                    <label style="display: flex; align-items: center; gap: 10px; padding: 12px; border: 1px solid var(--gray-300); border-radius: var(--radius); cursor: pointer;">
                        <input type="radio" name="paymentMethod" value="COD" />
                        <div>
                            <strong>Cash on Delivery (Verified)</strong>
                            <div style="font-size: 12px; color: var(--gray-500);">Pay upon parcel receipt</div>
                        </div>
                    </label>
                </div>
            </div>
        </div>

        <!-- Items & Grand Total Review -->
        <div>
            <div class="card">
                <h3 style="font-size: 18px; font-weight: 800; margin-bottom: 16px; border-bottom: 1px solid var(--gray-100); padding-bottom: 10px;">
                    Review Items
                </h3>

                <div style="display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; max-height: 260px; overflow-y: auto;">
                    <c:forEach var="item" items="${cartItems}">
                        <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px;">
                            <div style="max-width: 180px;">
                                <div style="font-weight: 600;"><c:out value="${item.productName}"/></div>
                                <div style="color: var(--gray-500);">Qty: <c:out value="${item.quantity}"/> × ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></div>
                            </div>
                            <div style="font-weight: 700;">
                                ₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div style="border-top: 1px solid var(--gray-300); padding-top: 12px; margin-bottom: 16px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px;">
                        <span>Subtotal:</span>
                        <span>₹<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14px;">
                        <span>GST (5%):</span>
                        <span>₹<fmt:formatNumber value="${tax}" pattern="#,##0.00"/></span>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 18px; font-weight: 800; color: var(--primary);">
                        <span>Total Due:</span>
                        <span>₹<fmt:formatNumber value="${total}" pattern="#,##0.00"/></span>
                    </div>
                </div>

                <button type="submit" class="btn btn-success" style="width: 100%; padding: 14px; font-size: 16px;">
                    💳 Confirm & Place Order
                </button>
            </div>
        </div>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
