<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sign In | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 440px; margin: 40px auto;">
    <div class="card">
        <h2 style="font-size: 24px; font-weight: 800; margin-bottom: 8px; text-align: center;">Welcome Back</h2>
        <p style="color: var(--gray-500); font-size: 14px; text-align: center; margin-bottom: 24px;">Sign in to your SahanaMart account</p>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <c:if test="${not empty returnUrl}">
                <input type="hidden" name="returnUrl" value="<c:out value='${returnUrl}'/>" />
            </c:if>

            <div class="form-group">
                <label for="loginEmail">Email Address</label>
                <input type="email" id="loginEmail" name="email" class="form-control" required
                       value="<c:out value='${email != null ? email : param.email}'/>" placeholder="name@example.com" />
            </div>

            <div class="form-group">
                <label for="loginPassword">Password</label>
                <input type="password" id="loginPassword" name="password" class="form-control" required
                       placeholder="••••••••" />
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Sign In</button>
        </form>

        <div style="margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--gray-300);">
            <p style="font-size: 12px; font-weight: 700; color: var(--gray-500); text-transform: uppercase; margin-bottom: 10px; text-align: center;">
                ⚡ 1-Click Demo Evaluation Accounts
            </p>
            <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1;"
                        onclick="fillCredentials('buyer@sahanamart.com', 'Buyer@123')">
                    Buyer
                </button>
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1;"
                        onclick="fillCredentials('seller@sahanamart.com', 'Seller@123')">
                    Seller
                </button>
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1;"
                        onclick="fillCredentials('admin@sahanamart.com', 'Admin@123')">
                    Admin
                </button>
            </div>
        </div>

        <p style="text-align: center; font-size: 14px; margin-top: 20px; color: var(--gray-500);">
            Don't have an account? <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 600;">Create one</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
