<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Orders | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 26px; font-weight: 800; margin-bottom: 24px;">Your Order History (F6)</h1>

<c:choose>
    <c:when test="${not empty orders}">
        <div style="display: flex; flex-direction: column; gap: 20px;">
            <c:forEach var="order" items="${orders}">
                <div class="card" style="margin-bottom: 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--gray-100); padding-bottom: 14px; margin-bottom: 16px;">
                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Order Number</span>
                            <div style="font-weight: 800; font-size: 16px; color: var(--primary);"><c:out value="${order.orderNumber}"/></div>
                        </div>

                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Date Placed</span>
                            <div style="font-size: 14px; color: var(--gray-700);"><c:out value="${order.createdAt}"/></div>
                        </div>

                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Total Amount</span>
                            <div style="font-weight: 800; font-size: 16px;">₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></div>
                        </div>

                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Workflow Status (O2)</span>
                            <div><span class="status-badge ${order.status}"><c:out value="${order.status}"/></span></div>
                        </div>
                    </div>

                    <!-- Ordered Items -->
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        <c:forEach var="item" items="${order.items}">
                            <div style="display: flex; justify-content: space-between; align-items: center; background: var(--gray-50); padding: 12px; border-radius: var(--radius);">
                                <div style="display: flex; align-items: center; gap: 12px;">
                                    <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 6px;" />
                                    <div>
                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 700; color: var(--gray-900);">
                                            <c:out value="${item.productName}"/>
                                        </a>
                                        <div style="font-size: 13px; color: var(--gray-500);">
                                            Qty: <c:out value="${item.quantity}"/> × ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                        </div>
                                    </div>
                                </div>

                                <div style="display: flex; align-items: center; gap: 16px;">
                                    <span style="font-weight: 700; font-size: 15px;">₹<fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00"/></span>

                                    <!-- Review Action for Delivered Orders (F8) -->
                                    <c:if test="${order.status == 'DELIVERED'}">
                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="btn btn-outline btn-sm" style="color: var(--accent); border-color: var(--accent);">
                                            ★ Rate & Review
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top: 14px; font-size: 13px; color: var(--gray-500);">
                        Shipping to: <strong style="color: var(--gray-700);"><c:out value="${order.shippingAddress}"/></strong>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 60px 20px;">
            <p style="font-size: 48px; margin-bottom: 12px;">📦</p>
            <h3 style="font-size: 20px; font-weight: 700; margin-bottom: 8px;">No orders found</h3>
            <p style="color: var(--gray-500); margin-bottom: 20px;">You haven't placed any orders yet.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse Catalog</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
