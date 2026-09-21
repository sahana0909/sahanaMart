<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Shopping Cart | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<h1 style="font-size: 26px; font-weight: 800; margin-bottom: 24px;">Your Shopping Cart</h1>

<c:choose>
    <c:when test="${not empty cartItems}">
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 30px;">
            <div>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Unit Price</th>
                                <th>Quantity</th>
                                <th>Subtotal</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cartItems}">
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 14px;">
                                            <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                                 style="width: 55px; height: 55px; object-fit: cover; border-radius: 6px;" />
                                            <div>
                                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}"
                                                   style="font-weight: 700; color: var(--gray-900);">
                                                    <c:out value="${item.productName}"/>
                                                </a>
                                                <div style="font-size: 12px; color: var(--gray-500); margin-top: 2px;">
                                                    Available stock: <c:out value="${item.stockAvailable}"/>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; align-items: center; gap: 6px;">
                                            <input type="hidden" name="cartItemId" value="${item.id}" />
                                            <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.stockAvailable}"
                                                   class="form-control" style="width: 70px; padding: 6px;" onchange="this.form.submit()" />
                                        </form>
                                    </td>
                                    <td style="font-weight: 700;">₹<fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart/remove" method="post" style="display: inline;">
                                            <input type="hidden" name="cartItemId" value="${item.id}" />
                                            <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger);">✕ Remove</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div style="margin-top: 16px; display: flex; justify-content: space-between;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">← Continue Shopping</a>
                    <form action="${pageContext.request.contextPath}/cart/clear" method="post">
                        <button type="submit" class="btn btn-outline" style="color: var(--danger);">Clear Entire Cart</button>
                    </form>
                </div>
            </div>

            <!-- Order Summary Card -->
            <div>
                <div class="card">
                    <h3 style="font-size: 18px; font-weight: 800; margin-bottom: 16px; border-bottom: 1px solid var(--gray-100); padding-bottom: 10px;">
                        Order Summary
                    </h3>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px;">
                        <span style="color: var(--gray-700);">Items Subtotal:</span>
                        <span style="font-weight: 600;">₹<fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 14px; font-size: 14px;">
                        <span style="color: var(--gray-700);">Standard GST (5%):</span>
                        <span style="font-weight: 600;">₹<fmt:formatNumber value="${tax}" pattern="#,##0.00"/></span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 20px; font-size: 18px; font-weight: 800; border-top: 1px dashed var(--gray-300); padding-top: 12px;">
                        <span>Estimated Total:</span>
                        <span style="color: var(--primary);">₹<fmt:formatNumber value="${total}" pattern="#,##0.00"/></span>
                    </div>

                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary" style="width: 100%; padding: 14px; font-size: 16px;">
                        Proceed to Checkout →
                    </a>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 60px 20px;">
            <p style="font-size: 52px; margin-bottom: 12px;">🛒</p>
            <h3 style="font-size: 20px; font-weight: 700; margin-bottom: 8px;">Your Cart is Empty</h3>
            <p style="color: var(--gray-500); margin-bottom: 24px;">Looks like you haven't added anything to your cart yet.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Start Shopping Now</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
