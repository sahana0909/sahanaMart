<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Explore Products | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- Category Filter Pills -->
<div class="category-nav" style="margin-bottom: 25px; border-radius: var(--radius);">
    <ul class="category-list">
        <li>
            <a href="${pageContext.request.contextPath}/products<c:if test='${not empty keyword}'>?keyword=<c:out value='${keyword}'/></c:if>"
               class="category-pill ${empty selectedCategoryId ? 'active' : ''}">
                All Categories
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

<!-- Header / Search status banner -->
<c:if test="${empty keyword && empty selectedCategoryId}">
    <div class="hero-banner">
        <h1>Discover Top Quality Goods</h1>
        <p>Browse the latest electronics, fashion, home essentials, and engineering literature.</p>
    </div>
</c:if>

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
    <h2 style="font-size: 20px; font-weight: 700;">
        <c:choose>
            <c:when test="${not empty keyword}">
                Search results for "<c:out value="${keyword}"/>" (<c:out value="${totalCount}"/> items)
            </c:when>
            <c:otherwise>
                Featured Catalog (<c:out value="${totalCount}"/> available)
            </c:otherwise>
        </c:choose>
    </h2>
</div>

<!-- Product Grid -->
<c:choose>
    <c:when test="${not empty products}">
        <div class="products-grid">
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                    <a href="${pageContext.request.contextPath}/product?id=${product.id}">
                        <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" class="product-image" loading="lazy" />
                    </a>
                    <div class="product-body">
                        <span class="product-category"><c:out value="${product.categoryName}"/></span>
                        <a href="${pageContext.request.contextPath}/product?id=${product.id}" class="product-title">
                            <c:out value="${product.name}"/>
                        </a>

                        <div class="product-rating">
                            <c:choose>
                                <c:when test="${product.reviewCount > 0}">
                                    <span>★ <fmt:formatNumber value="${product.averageRating}" pattern="0.0"/></span>
                                    <span style="color: var(--gray-500); font-size: 12px;">(<c:out value="${product.reviewCount}"/>)</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: var(--gray-500); font-size: 12px;">No reviews yet</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="product-footer">
                            <span class="product-price">₹<fmt:formatNumber value="${product.price}" pattern="#,##0.00"/></span>

                            <c:choose>
                                <c:when test="${product.stockQty > 0}">
                                    <form action="${pageContext.request.contextPath}/cart/add" method="post" style="display: inline;">
                                        <input type="hidden" name="productId" value="${product.id}" />
                                        <input type="hidden" name="quantity" value="1" />
                                        <button type="submit" class="btn btn-primary btn-sm">+ Add</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock-tag out-stock" style="margin-bottom: 0;">Sold Out</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div style="display: flex; justify-content: center; gap: 8px; margin-top: 40px;">
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
        <div class="card" style="text-align: center; padding: 60px 20px;">
            <p style="font-size: 48px; margin-bottom: 12px;">🔍</p>
            <h3 style="font-size: 20px; font-weight: 700; margin-bottom: 8px;">No products found</h3>
            <p style="color: var(--gray-500); margin-bottom: 20px;">Try searching with different terms or selecting another category.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Reset Filter</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
