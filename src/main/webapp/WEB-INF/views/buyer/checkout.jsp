<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Secure Checkout | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Complete Your Order</h1>
    <p style="font-size: 14px; color: var(--text-secondary); margin-top: 4px;">Provide your shipping details and select your preferred payment mode</p>
</div>

<form action="${pageContext.request.contextPath}/checkout/place-order" method="post">
    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 32px;">
        <div>
            <!-- Shipping Address Form Card -->
            <div class="card">
                <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 18px;">
                    <div style="width: 28px; height: 28px; border-radius: 50%; background: var(--primary); color: #FFFFFF; font-weight: 800; font-size: 14px; display: flex; align-items: center; justify-content: center;">
                        1
                    </div>
                    <h3 style="font-size: 18px; font-weight: 800; color: var(--text-main); margin: 0;">
                        Shipping & Delivery Address
                    </h3>
                </div>

                <div class="form-group">
                    <label for="shippingAddress">Full Delivery Address (Street, Apartment, City, State, PIN)</label>
                    <textarea id="shippingAddress" name="shippingAddress" rows="3" class="form-control" required
                              placeholder="e.g. Flat 304, Royal Towers, GST Road, Guindy, Chennai, Tamil Nadu - 600032"><c:out value="${shippingAddress != null ? shippingAddress : 'Flat 304, Royal Towers, GST Road, Guindy, Chennai, Tamil Nadu - 600032'}"/></textarea>
                </div>
            </div>

            <!-- Payment Method Card -->
            <div class="card">
                <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 12px;">
                    <div style="width: 28px; height: 28px; border-radius: 50%; background: var(--violet); color: #FFFFFF; font-weight: 800; font-size: 14px; display: flex; align-items: center; justify-content: center;">
                        2
                    </div>
                    <h3 style="font-size: 18px; font-weight: 800; color: var(--text-main); margin: 0;">
                        Payment Mode (Simulated Sandbox)
                    </h3>
                </div>

                <p style="font-size: 13px; color: var(--text-secondary); margin-bottom: 20px;">
                    Select an instant sandbox payment method for evaluation. No actual currency will be debited.
                </p>

                <div style="display: flex; flex-direction: column; gap: 12px;">
                    <label style="display: flex; align-items: center; gap: 14px; padding: 14px 18px; border: 1.5px solid var(--primary); border-radius: var(--radius); cursor: pointer; background: var(--primary-light); transition: var(--transition);">
                        <input type="radio" name="paymentMethod" value="MOCK_UPI" checked style="accent-color: var(--primary);" />
                        <div>
                            <strong style="color: var(--primary); font-size: 15px;">Simulated Instant UPI (Google Pay / PhonePe / Paytm)</strong>
                            <div style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">Zero fees • Instant automated verification</div>
                        </div>
                    </label>

                    <label style="display: flex; align-items: center; gap: 14px; padding: 14px 18px; border: 1.5px solid var(--border); border-radius: var(--radius); cursor: pointer; background: #FFFFFF; transition: var(--transition);">
                        <input type="radio" name="paymentMethod" value="MOCK_CARD" style="accent-color: var(--primary);" />
                        <div>
                            <strong style="color: var(--text-main); font-size: 15px;">Simulated Credit / Debit Card (Visa / Mastercard / RuPay)</strong>
                            <div style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">Instant 3D-Secure sandbox payment authorization</div>
                        </div>
                    </label>

                    <label style="display: flex; align-items: center; gap: 14px; padding: 14px 18px; border: 1.5px solid var(--border); border-radius: var(--radius); cursor: pointer; background: #FFFFFF; transition: var(--transition);">
                        <input type="radio" name="paymentMethod" value="COD" style="accent-color: var(--primary);" />
                        <div>
                            <strong style="color: var(--text-main); font-size: 15px;">Cash on Delivery (COD)</strong>
                            <div style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">Pay upon physical receipt at your delivery address</div>
                        </div>
                    </label>
                </div>
            </div>
        </div>

        <!-- Items & Grand Total Review Card -->
        <div>
            <div class="card" style="position: sticky; top: 96px;">
                <h3 style="font-size: 19px; font-weight: 800; margin-bottom: 18px; border-bottom: 1px solid var(--border); padding-bottom: 12px; color: var(--text-main);">
                    Order Summary
                </h3>

                <div style="display: flex; flex-direction: column; gap: 14px; margin-bottom: 18px; max-height: 280px; overflow-y: auto;">
                    <c:forEach var="item" items="${cartItems}">
                        <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13.5px;">
                            <div style="max-width: 190px;">
                                <div style="font-weight: 700; color: var(--text-main);"><c:out value="${item.productName}"/></div>
                                <div style="color: var(--text-secondary); font-size: 12px; margin-top: 2px;">
                                    Qty: <c:out value="${item.quantity}"/> × <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                </div>
                            </div>
                            <div style="font-weight: 800; color: var(--text-main);">
                                <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div style="border-top: 1px solid var(--border); padding-top: 14px; margin-bottom: 20px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px;">
                        <span style="color: var(--text-secondary);">Subtotal:</span>
                        <span style="font-weight: 700; color: var(--text-main);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/>
                        </span>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-bottom: 14px; font-size: 14px;">
                        <span style="color: var(--text-secondary);">GST (5%):</span>
                        <span style="font-weight: 700; color: var(--text-main);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${tax}" pattern="#,##0.00"/>
                        </span>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 20px; font-weight: 900; color: var(--primary); border-top: 1.5px dashed var(--border); padding-top: 14px;">
                        <span>Total Due:</span>
                        <span><span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${total}" pattern="#,##0.00"/></span>
                    </div>
                </div>

                <button type="submit" class="btn btn-success btn-lg" style="width: 100%; font-size: 16px;">
                    💳 Confirm & Place Order
                </button>
            </div>
        </div>
    </div>
</form>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
