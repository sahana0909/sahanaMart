<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Orders | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Your Order History</h1>
    <p style="font-size: 14px; color: var(--text-secondary); margin-top: 4px;">Track order fulfilment and write verified customer reviews</p>
</div>

<c:choose>
    <c:when test="${not empty orders}">
        <div style="display: flex; flex-direction: column; gap: 24px;">
            <c:forEach var="order" items="${orders}">
                <div class="card" style="margin-bottom: 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 16px; margin-bottom: 18px; flex-wrap: wrap; gap: 14px;">
                        <div>
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Order Number</span>
                            <div style="font-weight: 800; font-size: 16px; color: var(--primary); margin-top: 2px;"><c:out value="${order.orderNumber}"/></div>
                        </div>

                        <div>
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Date Placed</span>
                            <div style="font-size: 14px; color: var(--text-main); font-weight: 600; margin-top: 2px;"><c:out value="${order.createdAt}"/></div>
                        </div>

                        <div>
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Total Amount</span>
                            <div style="font-weight: 800; font-size: 17px; color: var(--text-main); margin-top: 2px;">
                                <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/>
                            </div>
                        </div>

                        <div>
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Order Status</span>
                            <div style="margin-top: 2px;"><span class="status-badge ${order.status}"><c:out value="${order.status}"/></span></div>
                        </div>
                    </div>

                    <!-- Ordered Items List -->
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        <c:forEach var="item" items="${order.items}">
                            <div style="display: flex; justify-content: space-between; align-items: center; background: var(--bg-muted); padding: 14px 18px; border-radius: var(--radius); border: 1px solid var(--border-light); flex-wrap: wrap; gap: 12px;">
                                <div style="display: flex; align-items: center; gap: 14px;">
                                    <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                         style="width: 52px; height: 52px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border);" />
                                    <div>
                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" style="font-weight: 700; color: var(--text-main); font-size: 14.5px;">
                                            <c:out value="${item.productName}"/>
                                        </a>
                                        <div style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">
                                            Qty: <c:out value="${item.quantity}"/> × <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                        </div>
                                    </div>
                                </div>

                                <div style="display: flex; align-items: center; gap: 18px;">
                                    <span style="font-weight: 800; font-size: 16px; color: var(--text-main);">
                                        <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00"/>
                                    </span>

                                    <!-- Review Action for Delivered Orders -->
                                    <c:if test="${order.status == 'DELIVERED'}">
                                        <a href="${pageContext.request.contextPath}/product?id=${item.productId}" class="btn btn-outline btn-sm" style="color: var(--violet); border-color: var(--violet);">
                                            ★ Rate & Review
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top: 16px; font-size: 13.5px; color: var(--text-secondary); display: flex; align-items: center; gap: 6px;">
                        <span>📍 Delivery Destination:</span>
                        <strong style="color: var(--text-main);"><c:out value="${order.shippingAddress}"/></strong>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 70px 24px;">
            <p style="font-size: 52px; margin-bottom: 16px;">📦</p>
            <h3 style="font-size: 22px; font-weight: 800; margin-bottom: 8px; color: var(--text-main);">No Orders Found</h3>
            <p style="color: var(--text-secondary); margin-bottom: 24px;">You haven't placed any orders yet.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Browse Marketplace Catalog</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
