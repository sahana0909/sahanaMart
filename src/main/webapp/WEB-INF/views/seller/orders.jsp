<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Incoming Orders | SahanaMart Seller" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 28px;">
    <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Incoming Customer Orders</h1>
    <p style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">Update shipment statuses and fulfill orders across fulfillment states</p>
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
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Buyer Details</span>
                            <div style="font-size: 14px; font-weight: 600; color: var(--text-main); margin-top: 2px;">
                                <c:out value="${order.buyerName}"/> <span style="color: var(--text-secondary); font-size: 13px;">(<c:out value="${order.buyerEmail}"/>)</span>
                            </div>
                        </div>

                        <div>
                            <span style="font-size: 11.5px; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; letter-spacing: 0.5px;">Current Status</span>
                            <div style="margin-top: 2px;"><span class="status-badge ${order.status}"><c:out value="${order.status}"/></span></div>
                        </div>

                        <!-- Order Status Workflow Update Form -->
                        <div>
                            <form action="${pageContext.request.contextPath}/seller/orders/status" method="post" style="display: flex; gap: 8px; align-items: center;">
                                <input type="hidden" name="orderId" value="${order.id}" />
                                <select name="status" class="form-control" style="width: 145px; padding: 7px 10px; font-size: 13px; font-weight: 600;">
                                    <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                    <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                                <button type="submit" class="btn btn-outline btn-sm" style="color: var(--primary); border-color: var(--primary);">
                                    Update
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Items in order -->
                    <div style="display: flex; flex-direction: column; gap: 12px;">
                        <c:forEach var="item" items="${order.items}">
                            <div style="display: flex; justify-content: space-between; align-items: center; background: var(--bg-muted); padding: 12px 18px; border-radius: var(--radius); border: 1px solid var(--border-light); flex-wrap: wrap; gap: 12px;">
                                <div style="display: flex; align-items: center; gap: 12px;">
                                    <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                         style="width: 44px; height: 44px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border);" />
                                    <div>
                                        <div style="font-weight: 700; font-size: 14.5px; color: var(--text-main);"><c:out value="${item.productName}"/></div>
                                        <div style="font-size: 12.5px; color: var(--text-secondary); margin-top: 2px;">
                                            Qty: <c:out value="${item.quantity}"/> × <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                        </div>
                                    </div>
                                </div>
                                <div style="font-weight: 800; font-size: 16px; color: var(--text-main);">
                                    <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00"/>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top: 14px; font-size: 13.5px; color: var(--text-secondary);">
                        Delivery Destination: <strong style="color: var(--text-main);"><c:out value="${order.shippingAddress}"/></strong>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 70px 24px;">
            <p style="font-size: 52px; margin-bottom: 16px;">📥</p>
            <h3 style="font-size: 22px; font-weight: 800; margin-bottom: 8px; color: var(--text-main);">No Incoming Orders</h3>
            <p style="color: var(--text-secondary);">Orders placed by buyers for your listed products will appear here in real time.</p>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
