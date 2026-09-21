<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page Not Found | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 520px; margin: 60px auto; text-align: center;">
    <div class="card" style="padding: 50px 30px;">
        <div style="font-size: 64px; margin-bottom: 16px;">🔍</div>
        <h1 style="font-size: 28px; font-weight: 800; margin-bottom: 10px;">404 - Page Not Found</h1>
        <p style="color: var(--gray-500); font-size: 15px; margin-bottom: 24px;">
            The page or product you were looking for does not exist or has been moved.
        </p>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
            Return to Storefront
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
