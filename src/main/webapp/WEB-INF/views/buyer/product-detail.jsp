<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${product.name} | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 24px;">
    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">
        ← Back to Catalog
    </a>
</div>

<div class="detail-container">
    <div>
        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" class="detail-image" />
    </div>

    <div class="detail-info">
        <span class="product-category"><c:out value="${product.categoryName}"/></span>
        <h1><c:out value="${product.name}"/></h1>

        <div class="product-rating" style="font-size: 15px; margin-bottom: 16px;">
            <c:choose>
                <c:when test="${reviewCount > 0}">
                    <span>★ <fmt:formatNumber value="${averageRating}" pattern="0.0"/> / 5.0</span>
                    <span style="color: var(--text-secondary); font-size: 13.5px; font-weight: 500;">
                        (<c:out value="${reviewCount}"/> verified customer reviews)
                    </span>
                </c:when>
                <c:otherwise>
                    <span style="color: var(--text-muted); font-size: 13.5px; font-weight: 500;">★ No customer reviews yet</span>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="detail-price">
            <span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
        </div>

        <div>
            <c:choose>
                <c:when test="${product.stockQty > 0}">
                    <span class="stock-tag in-stock">
                        <span style="font-size: 14px;">●</span> In Stock (<c:out value="${product.stockQty}"/> units available)
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="stock-tag out-stock">
                        <span style="font-size: 14px;">●</span> Currently Out of Stock
                    </span>
                </c:otherwise>
            </c:choose>
        </div>

        <p style="color: var(--text-main); line-height: 1.7; margin-bottom: 24px; font-size: 15px;">
            <c:out value="${product.description}"/>
        </p>

        <div style="background: var(--bg-muted); border: 1px solid var(--border); border-radius: var(--radius); padding: 14px 18px; margin-bottom: 24px;">
            <div style="font-size: 13px; color: var(--text-secondary);">
                Sold and dispatched by:
                <strong style="color: var(--primary); font-weight: 700;">
                    <c:out value="${product.sellerName}"/>
                </strong>
            </div>
            <div style="font-size: 12px; color: var(--teal); font-weight: 600; margin-top: 4px;">
                ✓ Verified SahanaMart Merchant
            </div>
        </div>

        <c:if test="${product.stockQty > 0}">
            <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: flex; gap: 14px; align-items: center; flex-wrap: wrap;">
                <input type="hidden" name="productId" value="${product.id}" />
                <div style="display: flex; align-items: center; gap: 8px;">
                    <label for="quantity" style="font-size: 14px; font-weight: 700; color: var(--text-main);">Qty:</label>
                    <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQty}"
                           class="form-control" style="width: 84px; text-align: center; font-weight: 700;" />
                </div>
                <button type="submit" class="btn btn-primary btn-lg" style="flex: 1; min-width: 200px;">
                    🛒 Add to Cart
                </button>
            </form>
        </c:if>
    </div>
</div>

<!-- Customer Reviews Section -->
<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
        <h2 style="font-size: 20px; font-weight: 800; color: var(--text-main);">Verified Customer Reviews</h2>
        <span style="font-size: 14px; color: var(--text-secondary);">
            Average Rating: <strong style="color: var(--warning);"><fmt:formatNumber value="${averageRating}" pattern="0.0"/> ★</strong> (<c:out value="${reviewCount}"/> reviews)
        </span>
    </div>

    <!-- Review Form if Buyer is verified purchaser -->
    <c:choose>
        <c:when test="${canReview}">
            <div style="background: var(--primary-light); padding: 24px; border-radius: var(--radius); margin-bottom: 28px; border: 1.5px dashed var(--primary);">
                <h3 style="font-size: 17px; font-weight: 800; color: var(--primary); margin-bottom: 8px;">Write a Customer Review</h3>
                <p style="font-size: 13.5px; color: var(--text-secondary); margin-bottom: 16px;">
                    You purchased and received this product! Share your honest feedback with fellow shoppers.
                </p>

                <form action="${pageContext.request.contextPath}/reviews/add" method="post">
                    <input type="hidden" name="productId" value="${product.id}" />
                    <input type="hidden" name="orderId" value="${eligibleOrderId}" />

                    <div class="form-group">
                        <label for="rating">Star Rating</label>
                        <select name="rating" id="rating" class="form-control" style="max-width: 240px; font-weight: 600;" required>
                            <option value="5">⭐⭐⭐⭐⭐ (5 - Outstanding)</option>
                            <option value="4">⭐⭐⭐⭐ (4 - Very Good)</option>
                            <option value="3">⭐⭐⭐ (3 - Average)</option>
                            <option value="2">⭐⭐ (2 - Below Expectations)</option>
                            <option value="1">⭐ (1 - Poor)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="comment">Review Comments</label>
                        <textarea name="comment" id="comment" rows="3" class="form-control" required
                                  placeholder="Describe the build quality, performance, and overall satisfaction..."></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Submit Review</button>
                </form>
            </div>
        </c:when>
        <c:when test="${empty sessionScope.currentUser}">
            <div style="font-size: 13.5px; color: var(--text-secondary); margin-bottom: 24px; background: var(--bg-muted); padding: 14px 18px; border-radius: var(--radius); border: 1px solid var(--border);">
                💡 Only verified buyers who purchased this product can submit a review. <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 700;">Sign in to your account</a>.
            </div>
        </c:when>
    </c:choose>

    <!-- Review List -->
    <c:choose>
        <c:when test="${not empty reviews}">
            <div style="display: flex; flex-direction: column; gap: 18px;">
                <c:forEach var="review" items="${reviews}">
                    <div style="background: var(--bg-muted); border: 1px solid var(--border); border-radius: var(--radius); padding: 18px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <div style="width: 32px; height: 32px; border-radius: 50%; background: var(--primary-light); color: var(--primary); font-weight: 800; font-size: 13px; display: flex; align-items: center; justify-content: center;">
                                    ${review.buyerName.substring(0, 1)}
                                </div>
                                <span style="font-weight: 700; font-size: 14.5px; color: var(--text-main);"><c:out value="${review.buyerName}"/></span>
                                <span style="font-size: 11px; background: var(--success-light); color: var(--success-text); padding: 2px 6px; border-radius: 4px; font-weight: 700;">✓ Verified Buyer</span>
                            </div>
                            <span style="color: var(--warning); font-size: 14px;">
                                <c:forEach begin="1" end="${review.rating}">★</c:forEach>
                            </span>
                        </div>
                        <p style="color: var(--text-main); font-size: 14px; line-height: 1.6; padding-left: 40px;">
                            <c:out value="${review.comment}"/>
                        </p>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div style="color: var(--text-secondary); font-size: 14px; text-align: center; padding: 30px 20px;">
                <p style="font-size: 32px; margin-bottom: 8px;">⭐</p>
                <p>No reviews yet for this product. Be the first verified buyer to leave a rating!</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
