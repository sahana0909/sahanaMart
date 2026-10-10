<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Sign In | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 460px; margin: 44px auto;">
    <div class="card" style="border-top: 4px solid var(--primary); box-shadow: var(--shadow-md);">
        <div style="text-align: center; margin-bottom: 24px;">
            <div style="width: 52px; height: 52px; border-radius: 16px; background: var(--gradient-brand); color: #FFFFFF; font-size: 26px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 12px; box-shadow: 0 4px 12px var(--primary-glow);">
                🛍️
            </div>
            <h2 style="font-size: 26px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Welcome Back</h2>
            <p style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">Sign in to access your SahanaMart shopping account</p>
        </div>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <c:if test="${not empty returnUrl}">
                <input type="hidden" name="returnUrl" value="<c:out value='${returnUrl}'/>" />
            </c:if>

            <div class="form-group">
                <label for="loginEmail">Email Address</label>
                <input type="email" id="loginEmail" name="email" class="form-control" required
                       value="<c:out value='${email != null ? email : param.email}'/>" placeholder="name@example.com" autocomplete="email" />
            </div>

            <div class="form-group">
                <label for="loginPassword">Password</label>
                <input type="password" id="loginPassword" name="password" class="form-control" required
                       placeholder="••••••••" autocomplete="current-password" />
            </div>

            <button type="submit" class="btn btn-primary btn-lg" style="width: 100%; margin-top: 8px;">
                Sign In to Account
            </button>
        </form>

        <div style="margin-top: 28px; padding-top: 20px; border-top: 1px solid var(--border);">
            <p style="font-size: 11.5px; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.6px; margin-bottom: 12px; text-align: center;">
                ⚡ 1-Click Evaluation Demo Logins
            </p>
            <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1; color: var(--primary); border-color: rgba(36, 71, 216, 0.3);"
                        onclick="fillCredentials('buyer@sahanamart.com', 'Buyer@123')">
                    Buyer
                </button>
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1; color: var(--teal-hover); border-color: rgba(15, 186, 166, 0.3);"
                        onclick="fillCredentials('seller@sahanamart.com', 'Seller@123')">
                    Seller
                </button>
                <button type="button" class="btn btn-outline btn-sm" style="flex: 1; color: var(--violet); border-color: rgba(124, 58, 237, 0.3);"
                        onclick="fillCredentials('admin@sahanamart.com', 'Admin@123')">
                    Admin
                </button>
            </div>
        </div>

        <p style="text-align: center; font-size: 14px; margin-top: 22px; color: var(--text-secondary);">
            Don't have an account? <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 700;">Create one now</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
