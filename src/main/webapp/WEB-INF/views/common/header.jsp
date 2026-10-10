<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setLocale value="en_IN" scope="session"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'SahanaMart | Premier Online Marketplace'}"/></title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script>window.CONTEXT_PATH = "${pageContext.request.contextPath}";</script>
    <script defer src="${pageContext.request.contextPath}/static/js/app.js"></script>
</head>
<body>
<header>
    <div class="container navbar">
        <a href="${pageContext.request.contextPath}/products" class="brand" title="SahanaMart Home">
            <span class="brand-icon-wrapper">
                <span class="brand-icon">🛍️</span>
            </span>
            <span class="brand-text">SahanaMart</span>
        </a>

        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/products" method="get">
                <input type="text" name="keyword" placeholder="Search laptops, smartphones, fashion, books..."
                       value="<c:out value='${param.keyword}'/>" autocomplete="off" />
                <c:if test="${not empty param.categoryId}">
                    <input type="hidden" name="categoryId" value="<c:out value='${param.categoryId}'/>" />
                </c:if>
                <button type="submit">
                    <span>Search</span>
                </button>
            </form>
        </div>

        <nav class="nav-links">
            <a href="${pageContext.request.contextPath}/products" class="nav-item">Explore</a>

            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <c:if test="${sessionScope.currentUser.role == 'BUYER'}">
                        <a href="${pageContext.request.contextPath}/cart" class="cart-btn" title="View Shopping Cart">
                            <span>🛒 Cart</span>
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
