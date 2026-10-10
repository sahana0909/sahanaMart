<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Discover Top Products | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Hero Banner (Shown on default storefront browse) -->
<c:if test="${empty keyword && empty selectedCategoryId}">
    <div class="hero-banner">
        <div class="hero-banner-content">
            <div class="hero-badge-tag">
                <span>✨ Premier E-Commerce Marketplace</span>
            </div>
            <h1>Quality Products,<br>Delivered With Confidence.</h1>
            <p>
                Shop authentic electronics, contemporary fashion, home essentials, and best-selling books at competitive Indian Rupee prices with prompt delivery.
            </p>
            <div class="hero-features">
                <div class="hero-feature-item">
                    <span>⚡ Express Shipping</span>
                </div>
                <div class="hero-feature-item">
                    <span>🔒 100% Secure Checkout</span>
                </div>
                <div class="hero-feature-item">
                    <span>🤖 AI Shopping Assistant</span>
                </div>
                <div class="hero-feature-item">
                    <span>🛡️ Verified Authentic Sellers</span>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-- Category Filter Navigation Pills -->
<div class="category-nav">
    <ul class="category-list">
        <li>
            <a href="${pageContext.request.contextPath}/products<c:if test='${not empty keyword}'>?keyword=<c:out value='${keyword}'/></c:if>"
               class="category-pill ${empty selectedCategoryId ? 'active' : ''}">
                ✨ All Categories
            </a>
        </li>
        <c:forEach var="cat" items="${categories}">
            <li>
                <a href="${pageContext.request.contextPath}/products?categoryId=${cat.id}<c:if test='${not empty keyword}'>&keyword=<c:out value='${keyword}'/></c:if>"
                   class="category-pill ${selectedCategoryId == cat.id ? 'active' : ''}">
                    <c:out value="${cat.name}"/>
                </a>
            </li>
        </c:forEach>
    </ul>
</div>

<!-- Header / Search Status Toolbar -->
<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 12px;">
    <div>
        <h2 style="font-size: 22px; font-weight: 800; color: var(--text-main); letter-spacing: -0.4px;">
            <c:choose>
                <c:when test="${not empty keyword}">
                    Search results for "<c:out value="${keyword}"/>"
                </c:when>
                <c:when test="${not empty selectedCategoryId}">
                    Category Catalog
                </c:when>
                <c:otherwise>
                    Featured Collections
                </c:otherwise>
            </c:choose>
        </h2>
        <span style="font-size: 13.5px; color: var(--text-secondary);">
            Showing <c:out value="${products.size()}"/> of <c:out value="${totalCount}"/> available items
        </span>
    </div>

    <c:if test="${not empty keyword || not empty selectedCategoryId}">
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">
            ✕ Clear Filters
        </a>
    </c:if>
</div>

<!-- Product Grid -->
<c:choose>
    <c:when test="${not empty products}">
        <div class="products-grid">
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                    <a href="${pageContext.request.contextPath}/product?id=${product.id}" class="product-image-wrapper">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" class="product-image" loading="lazy" />
                    </a>
                    <div class="product-body">
                        <span class="product-category"><c:out value="${product.categoryName}"/></span>
                        <a href="${pageContext.request.contextPath}/product?id=${product.id}" class="product-title" title="<c:out value='${product.name}'/>">
                            <c:out value="${product.name}"/>
                        </a>

                        <div class="product-rating">
                            <c:choose>
                                <c:when test="${product.reviewCount > 0}">
                                    <span>★ <fmt:formatNumber value="${product.averageRating}" pattern="0.0"/></span>
                                    <span style="color: var(--text-secondary); font-size: 12px; font-weight: 500;">(<c:out value="${product.reviewCount}"/> reviews)</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: var(--text-muted); font-size: 12px; font-weight: 500;">★ No reviews yet</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="product-footer">
                            <span class="product-price"><span class="currency-symbol">&#8377;</span><fmt:formatNumber value="${product.price}" pattern="#,##0.00"/></span>

                            <c:choose>
                                <c:when test="${product.stockQty > 0}">
                                    <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: inline;">
                                        <input type="hidden" name="productId" value="${product.id}" />
                                        <input type="hidden" name="quantity" value="1" />
                                        <button type="submit" class="btn btn-primary btn-sm" title="Add to Cart">
                                            + Add
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock-tag out-stock" style="margin-bottom: 0; padding: 4px 10px; font-size: 11px;">Sold Out</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div style="display: flex; justify-content: center; gap: 8px; margin-top: 44px;">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/products?page=${p}<c:if test='${not empty keyword}'>&keyword=<c:out value='${keyword}'/></c:if><c:if test='${not empty selectedCategoryId}'>&categoryId=${selectedCategoryId}</c:if>"
                       class="btn btn-sm ${currentPage == p ? 'btn-primary' : 'btn-outline'}">
                        ${p}
                    </a>
                </c:forEach>
            </div>
        </c:if>
    </c:when>
    <c:otherwise>
        <div class="card" style="text-align: center; padding: 70px 24px;">
            <p style="font-size: 52px; margin-bottom: 16px;">🔍</p>
            <h3 style="font-size: 22px; font-weight: 800; margin-bottom: 10px; color: var(--text-main);">No matching products found</h3>
            <p style="color: var(--text-secondary); margin-bottom: 24px; max-width: 460px; margin-left: auto; margin-right: auto;">
                We couldn't find any products matching your search criteria. Try using broader keywords or explore other categories.
            </p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                Browse All Products
            </a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
