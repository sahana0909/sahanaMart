<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Order Confirmed | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 680px; margin: 30px auto;">
    <div class="card" style="text-align: center; padding: 40px 30px;">
        <div style="font-size: 56px; margin-bottom: 12px;">🎉</div>
        <h1 style="font-size: 26px; font-weight: 800; color: var(--success); margin-bottom: 8px;">Order Placed Successfully!</h1>
        <p style="color: var(--gray-500); font-size: 15px; margin-bottom: 24px;">
            Thank you for shopping with SahanaMart. Your mock payment has been processed and your order is confirmed.
        </p>

        <div style="background: var(--gray-50); border: 1px solid var(--gray-300); border-radius: var(--radius); padding: 20px; text-align: left; margin-bottom: 24px;">
            <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px;">
                <span style="color: var(--gray-500);">Order Tracking Number:</span>
                <strong style="color: var(--primary);"><c:out value="${order.orderNumber}"/></strong>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px;">
                <span style="color: var(--gray-500);">Current Status:</span>
                <span class="status-badge ${order.status}"><c:out value="${order.status}"/></span>
            </div>

            <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px;">
                <span style="color: var(--gray-500);">Total Paid:</span>
                <strong style="font-size: 16px;">₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></strong>
            </div>

            <div style="font-size: 14px; margin-top: 10px; padding-top: 10px; border-top: 1px solid var(--gray-300);">
                <div style="color: var(--gray-500); margin-bottom: 4px;">Delivering to:</div>
                <div style="color: var(--gray-700);"><c:out value="${order.shippingAddress}"/></div>
            </div>
        </div>

        <div style="display: flex; gap: 14px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/buyer/orders" class="btn btn-primary">View My Orders History</a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">Continue Shopping</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
