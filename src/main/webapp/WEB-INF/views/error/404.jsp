<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page Not Found | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 520px; margin: 60px auto; text-align: center;">
    <div class="card" style="padding: 56px 36px;">
        <div style="font-size: 64px; margin-bottom: 16px;">🔍</div>
        <h1 style="font-size: 28px; font-weight: 800; color: var(--text-main); margin-bottom: 10px;">404 - Page Not Found</h1>
        <p style="color: var(--text-secondary); font-size: 15px; margin-bottom: 26px;">
            The page or product listing you were looking for does not exist or has been moved.
        </p>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
            Return to Storefront
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
