<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container" style="margin-top: 15px;">
    <%-- Request / Session Errors --%>
    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${error}"/></span>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${param.error}"/></span>
        </div>
    </c:if>
    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${sessionScope.cartError}"/></span>
        </div>
        <c:remove var="cartError" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.orderError}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${sessionScope.orderError}"/></span>
        </div>
        <c:remove var="orderError" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.sellerError}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${sessionScope.sellerError}"/></span>
        </div>
        <c:remove var="sellerError" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.adminError}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${sessionScope.adminError}"/></span>
        </div>
        <c:remove var="adminError" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.reviewError}">
        <div class="alert alert-error">
            <span>❌ <c:out value="${sessionScope.reviewError}"/></span>
        </div>
        <c:remove var="reviewError" scope="session" />
    </c:if>

    <%-- Success messages --%>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            <span>✅ <c:out value="${param.success}"/></span>
        </div>
    </c:if>
    <c:if test="${not empty sessionScope.cartSuccess}">
        <div class="alert alert-success">
            <span>✅ <c:out value="${sessionScope.cartSuccess}"/></span>
        </div>
        <c:remove var="cartSuccess" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.orderSuccess}">
        <div class="alert alert-success">
            <span>✅ <c:out value="${sessionScope.orderSuccess}"/></span>
        </div>
        <c:remove var="orderSuccess" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.sellerSuccess}">
        <div class="alert alert-success">
            <span>✅ <c:out value="${sessionScope.sellerSuccess}"/></span>
        </div>
        <c:remove var="sellerSuccess" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.adminSuccess}">
        <div class="alert alert-success">
            <span>✅ <c:out value="${sessionScope.adminSuccess}"/></span>
        </div>
        <c:remove var="adminSuccess" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.reviewSuccess}">
        <div class="alert alert-success">
            <span>⭐ <c:out value="${sessionScope.reviewSuccess}"/></span>
        </div>
        <c:remove var="reviewSuccess" scope="session" />
    </c:if>
</div>
