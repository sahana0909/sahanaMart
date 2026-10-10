<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Order Confirmed | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 680px; margin: 36px auto;">
    <div class="card" style="text-align: center; padding: 48px 36px;">
        <div style="font-size: 60px; margin-bottom: 16px;">🎉</div>
        <h1 style="font-size: 28px; font-weight: 800; color: var(--success); margin-bottom: 8px;">Order Placed Successfully!</h1>
        <p style="color: var(--text-secondary); font-size: 15px; margin-bottom: 28px;">
            Thank you for shopping on SahanaMart. Your order has been registered and is now being processed.
        </p>

        <div style="background: var(--bg-muted); border: 1px solid var(--border); border-radius: var(--radius); padding: 24px; text-align: left; margin-bottom: 28px;">
            <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14.5px;">
                <span style="color: var(--text-secondary);">Tracking Reference:</span>
                <strong style="color: var(--primary); font-size: 15px;"><c:out value="${order.orderNumber}"/></strong>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14.5px;">
                <span style="color: var(--text-secondary);">Fulfillment Status:</span>
                <span class="status-badge ${order.status}"><c:out value="${order.status}"/></span>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14.5px;">
                <span style="color: var(--text-secondary);">Total Paid:</span>
                <strong style="font-size: 17px; color: var(--text-main);">
                    <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/>
                </strong>
            </div>

            <div style="font-size: 14px; margin-top: 14px; padding-top: 14px; border-top: 1px solid var(--border);">
                <div style="color: var(--text-secondary); margin-bottom: 4px; font-size: 12.5px; text-transform: uppercase; font-weight: 700;">Delivering to:</div>
                <div style="color: var(--text-main); font-weight: 600;"><c:out value="${order.shippingAddress}"/></div>
            </div>
        </div>

        <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
            <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-primary btn-lg">
                View My Orders History
            </a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-lg">
                Continue Shopping
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
