<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${product.name} | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="margin-bottom: 20px;">
    <a href="${pageContext.request.contextPath}/products" style="color: var(--gray-500); font-size: 14px; font-weight: 600;">
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

        <div class="product-rating" style="font-size: 16px; margin-bottom: 16px;">
            <c:choose>
                <c:when test="${reviewCount > 0}">
                    <span>★ <fmt:formatNumber value="${averageRating}" pattern="0.0"/> / 5.0</span>
                    <span style="color: var(--gray-500); font-size: 14px;">(<c:out value="${reviewCount}"/> verified customer reviews)</span>
                </c:when>
                <c:otherwise>
                    <span style="color: var(--gray-500); font-size: 14px;">★ No customer reviews yet</span>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="detail-price">
            ₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
        </div>

        <div>
            <c:choose>
                <c:when test="${product.stockQty > 0}">
                    <span class="stock-tag in-stock">✓ In Stock (<c:out value="${product.stockQty}"/> units available)</span>
                </c:when>
                <c:otherwise>
                    <span class="stock-tag out-stock">✗ Currently Out of Stock</span>
                </c:otherwise>
            </c:choose>
        </div>

        <p style="color: var(--gray-700); line-height: 1.6; margin-bottom: 24px; font-size: 15px;">
            <c:out value="${product.description}"/>
        </p>

        <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 24px;">
            Sold and dispatched by: <strong style="color: var(--gray-700);"><c:out value="${product.sellerName}"/></strong>
        </p>

        <c:if test="${product.stockQty > 0}">
            <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: flex; gap: 12px; align-items: center;">
                <input type="hidden" name="productId" value="${product.id}" />
                <label for="quantity" style="font-size: 14px; font-weight: 600;">Qty:</label>
                <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQty}"
                       class="form-control" style="width: 80px;" />
                <button type="submit" class="btn btn-primary" style="flex: 1; padding: 12px 24px; font-size: 16px;">
                    🛒 Add to Cart
                </button>
            </form>
        </c:if>
    </div>
</div>

<!-- Customer Reviews Section (F8) -->
<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h2 style="font-size: 20px; font-weight: 800;">Customer Reviews & Ratings (F8)</h2>
        <span style="font-size: 14px; color: var(--gray-500);">
            Average Rating: <strong><fmt:formatNumber value="${averageRating}" pattern="0.0"/> ★</strong> (<c:out value="${reviewCount}"/> reviews)
        </span>
    </div>

    <!-- Review Form if Buyer is verified purchaser -->
    <c:choose>
        <c:when test="${canReview}">
            <div style="background: var(--gray-50); padding: 20px; border-radius: var(--radius); margin-bottom: 24px; border: 1px dashed var(--primary);">
                <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 8px;">Leave a Verified Customer Review</h3>
                <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 14px;">
                    You purchased and received this product! Share your feedback with other buyers.
                </p>

                <form action="${pageContext.request.contextPath}/reviews/add" method="post">
                    <input type="hidden" name="productId" value="${product.id}" />
                    <!-- Find recent orderId or allow selection -->
                    <c:forEach var="item" items="${buyerCompletedOrders}">
                        <input type="hidden" name="orderId" value="${item.id}" />
                    </c:forEach>

                    <div class="form-group">
                        <label for="rating">Star Rating</label>
                        <select name="rating" id="rating" class="form-control" style="max-width: 200px;" required>
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
                                  placeholder="What did you like or dislike about this product?"></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary btn-sm">Submit Review</button>
                </form>
            </div>
        </c:when>
        <c:when test="${empty sessionScope.currentUser}">
            <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 20px; background: var(--gray-100); padding: 12px; border-radius: var(--radius);">
                💡 Only verified buyers who purchased this item can leave a review. <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign in to your account</a>.
            </p>
        </c:when>
    </c:choose>

    <!-- Review List -->
    <c:choose>
        <c:when test="${not empty reviews}">
            <div style="display: flex; flex-direction: column; gap: 16px;">
                <c:forEach var="review" items="${reviews}">
                    <div style="border-bottom: 1px solid var(--gray-100); padding-bottom: 16px;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                            <span style="font-weight: 700; font-size: 14px;"><c:out value="${review.buyerName}"/></span>
                            <span style="color: var(--accent); font-size: 14px;">
                                <c:forEach begin="1" end="${review.rating}">★</c:forEach>
                            </span>
                        </div>
                        <p style="color: var(--gray-700); font-size: 14px; line-height: 1.5;">
                            <c:out value="${review.comment}"/>
                        </p>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <p style="color: var(--gray-500); font-size: 14px; text-align: center; padding: 20px;">
                No reviews yet. Be the first verified buyer to rate this product!
            </p>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
