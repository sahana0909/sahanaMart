<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Incoming Orders | SahanaMart Seller" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 26px; font-weight: 800; margin-bottom: 24px;">Incoming Customer Orders (F6 & O2)</h1>

<c:choose>
    <c:when test="${not empty orders}">
        <div style="display: flex; flex-direction: column; gap: 20px;">
            <c:forEach var="order" items="${orders}">
                <div class="card" style="margin-bottom: 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--gray-100); padding-bottom: 14px; margin-bottom: 16px;">
                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Order #</span>
                            <div style="font-weight: 800; font-size: 16px; color: var(--primary);"><c:out value="${order.orderNumber}"/></div>
                        </div>

                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Buyer</span>
                            <div style="font-size: 14px; font-weight: 600;"><c:out value="${order.buyerName}"/> (<c:out value="${order.buyerEmail}"/>)</div>
                        </div>

                        <div>
                            <span style="font-size: 12px; color: var(--gray-500); text-transform: uppercase;">Current Status</span>
                            <div><span class="status-badge ${order.status}"><c:out value="${order.status}"/></span></div>
                        </div>

                        <!-- O2 Order Status Workflow Actions -->
                        <div>
                            <form action="${pageContext.request.contextPath}/seller/orders/status" method="post" style="display: flex; gap: 8px; align-items: center;">
                                <input type="hidden" name="orderId" value="${order.id}" />
                                <select name="status" class="form-control" style="width: 140px; padding: 6px;">
                                    <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                    <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                                <button type="submit" class="btn btn-outline btn-sm">Update</button>
                            </form>
                        </div>
                    </div>

                    <!-- Items in order -->
                    <div style="display: flex; flex-direction: column; gap: 10px;">
                        <c:forEach var="item" items="${order.items}">
                            <div style="display: flex; justify-content: space-between; align-items: center; background: var(--gray-50); padding: 10px 14px; border-radius: var(--radius);">
                                <div style="display: flex; align-items: center; gap: 10px;">
                                    <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                         style="width: 40px; height: 40px; object-fit: cover; border-radius: 4px;" />
                                    <div>
                                        <div style="font-weight: 700; font-size: 14px;"><c:out value="${item.productName}"/></div>
                                        <div style="font-size: 12px; color: var(--gray-500);">Qty: <c:out value="${item.quantity}"/> × ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></div>
                                    </div>
                                </div>
                                <div style="font-weight: 700;">₹<fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.00"/></div>
                            </div>
                        </c:forEach>
                    </div>

                    <div style="margin-top: 12px; font-size: 13px; color: var(--gray-500);">
                        Delivery Destination: <strong style="color: var(--gray-700);"><c:out value="${order.shippingAddress}"/></strong>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 60px 20px;">
            <p style="font-size: 48px; margin-bottom: 12px;">📥</p>
            <h3 style="font-size: 20px; font-weight: 700; margin-bottom: 8px;">No incoming orders</h3>
            <p style="color: var(--gray-500);">Orders containing your products will appear here once buyers place them.</p>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
