<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'SahanaMart | Premier Online Marketplace'}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script defer src="${pageContext.request.contextPath}/static/js/app.js"></script>
</head>
<body>
<header>
    <div class="container navbar">
        <a href="${pageContext.request.contextPath}/products" class="brand">
            <span class="brand-icon">🛍️</span>
            <span>SahanaMart</span>
        </a>

        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/products" method="get">
                <input type="text" name="keyword" placeholder="Search laptops, clothing, coffee makers..."
                       value="<c:out value='${param.keyword}'/>" />
                <c:if test="${not empty param.categoryId}">
                    <input type="hidden" name="categoryId" value="<c:out value='${param.categoryId}'/>" />
                </c:if>
                <button type="submit">Search</button>
            </form>
        </div>

        <nav class="nav-links">
            <a href="${pageContext.request.contextPath}/products" class="nav-item">Browse</a>

            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <c:if test="${sessionScope.currentUser.role == 'BUYER'}">
                        <a href="${pageContext.request.contextPath}/cart" class="cart-btn">
                            🛒 Cart
                            <span class="cart-badge"><c:out value="${sessionScope.cartCount != null ? sessionScope.cartCount : 0}"/></span>
                        </a>
                        <a href="${pageContext.request.contextPath}/buyer/orders" class="nav-item">My Orders</a>
                    </c:if>

                    <c:if test="${sessionScope.currentUser.role == 'SELLER'}">
                        <a href="${pageContext.request.contextPath}/seller/dashboard" class="nav-item">Seller Dashboard</a>
                        <a href="${pageContext.request.contextPath}/seller/orders" class="nav-item">Incoming Orders</a>
                    </c:if>

                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item">Admin Panel</a>
                    </c:if>

                    <span class="role-badge ${sessionScope.currentUser.role}"><c:out value="${sessionScope.currentUser.role}"/></span>
                    <span class="nav-item" style="color: var(--primary); font-weight: 700;">
                        <c:out value="${sessionScope.currentUser.fullName}"/>
                    </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="nav-item">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Get Started</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />
<main class="container">
