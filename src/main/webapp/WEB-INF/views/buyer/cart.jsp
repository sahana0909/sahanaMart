<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Shopping Cart | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 24px;">
    <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Your Shopping Cart</h1>
    <p style="font-size: 14px; color: var(--text-secondary); margin-top: 4px;">Review items, adjust quantities, and proceed to secure checkout</p>
</div>

<c:choose>
    <c:when test="${not empty cartItems}">
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 32px;">
            <div>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Product Details</th>
                                <th>Unit Price</th>
                                <th>Quantity</th>
                                <th>Subtotal</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cartItems}">
                                <tr>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 16px;">
                                            <img src="<c:out value='${item.productImageUrl}'/>" alt="<c:out value='${item.productName}'/>"
                                                 style="width: 60px; height: 60px; object-fit: cover; border-radius: 10px; border: 1px solid var(--border);" />
                                            <div>
                                                <a href="${pageContext.request.contextPath}/product?id=${item.productId}"
                                                   style="font-weight: 700; color: var(--text-main); font-size: 15px; display: inline-block;">
                                                    <c:out value="${item.productName}"/>
                                                </a>
                                                <div style="font-size: 12.5px; color: var(--teal); font-weight: 600; margin-top: 3px;">
                                                    In Stock: <c:out value="${item.stockAvailable}"/> units
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td style="font-weight: 600;">
                                        <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart/update" method="post" style="display: flex; align-items: center; gap: 6px;">
                                            <input type="hidden" name="cartItemId" value="${item.id}" />
                                            <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.stockAvailable}"
                                                   class="form-control" style="width: 72px; padding: 7px 10px; text-align: center; font-weight: 700;" onchange="this.form.submit()" />
                                        </form>
                                    </td>
                                    <td style="font-weight: 800; color: var(--primary);">
                                        <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${item.subtotal}" pattern="#,##0.00"/>
                                    </td>
                                    <td style="text-align: right;">
                                        <form action="${pageContext.request.contextPath}/cart/remove" method="post" style="display: inline;">
                                            <input type="hidden" name="cartItemId" value="${item.id}" />
                                            <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: #FECACA;" title="Remove Item">
                                                ✕ Remove
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div style="margin-top: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">
                        ← Continue Shopping
                    </a>
                    <form action="${pageContext.request.contextPath}/cart/clear" method="post" onsubmit="return confirm('Are you sure you want to clear your entire cart?');">
                        <button type="submit" class="btn btn-outline" style="color: var(--danger);">
                            Clear Entire Cart
                        </button>
                    </form>
                </div>
            </div>

            <!-- Order Summary Card -->
            <div>
                <div class="card" style="position: sticky; top: 96px;">
                    <h3 style="font-size: 19px; font-weight: 800; margin-bottom: 20px; border-bottom: 1px solid var(--border); padding-bottom: 12px; color: var(--text-main);">
                        Order Summary
                    </h3>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14.5px;">
                        <span style="color: var(--text-secondary);">Items Subtotal:</span>
                        <span style="font-weight: 700; color: var(--text-main);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/>
                        </span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 16px; font-size: 14.5px;">
                        <span style="color: var(--text-secondary);">Standard GST (5%):</span>
                        <span style="font-weight: 700; color: var(--text-main);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${tax}" pattern="#,##0.00"/>
                        </span>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 24px; font-size: 20px; font-weight: 900; border-top: 1.5px dashed var(--border); padding-top: 16px;">
                        <span style="color: var(--text-main);">Total Due:</span>
                        <span style="color: var(--primary);">
                            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${total}" pattern="#,##0.00"/>
                        </span>
                    </div>

                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-lg" style="width: 100%; font-size: 16px;">
                        Proceed to Checkout →
                    </a>

                    <div style="margin-top: 18px; text-align: center; font-size: 12.5px; color: var(--text-secondary); display: flex; align-items: center; justify-content: center; gap: 6px;">
                        <span>🔒 100% Encrypted & Secure Checkout</span>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 70px 24px;">
            <p style="font-size: 56px; margin-bottom: 16px;">🛒</p>
            <h3 style="font-size: 22px; font-weight: 800; margin-bottom: 8px; color: var(--text-main);">Your Cart is Currently Empty</h3>
            <p style="color: var(--text-secondary); margin-bottom: 26px; max-width: 440px; margin-left: auto; margin-right: auto;">
                You haven't added any products to your shopping cart yet. Discover high-quality items in our catalog!
            </p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
                Explore Marketplace Catalog
            </a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
