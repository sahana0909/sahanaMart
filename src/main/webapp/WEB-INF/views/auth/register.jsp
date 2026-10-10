<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create Account | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 500px; margin: 44px auto;">
    <div class="card" style="border-top: 4px solid var(--violet); box-shadow: var(--shadow-md);">
        <div style="text-align: center; margin-bottom: 24px;">
            <div style="width: 52px; height: 52px; border-radius: 16px; background: linear-gradient(135deg, var(--violet) 0%, var(--teal) 100%); color: #FFFFFF; font-size: 26px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 12px; box-shadow: 0 4px 12px var(--violet-glow);">
                ✨
            </div>
            <h2 style="font-size: 26px; font-weight: 800; color: var(--text-main); letter-spacing: -0.5px;">Join SahanaMart</h2>
            <p style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">Register a customer buyer or merchant seller account</p>
        </div>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" required
                       value="<c:out value='${form.fullName}'/>" placeholder="e.g. Sahana Devi" />
                <c:if test="${not empty fieldErrors.fullName}">
                    <div class="form-error"><c:out value="${fieldErrors.fullName}"/></div>
                </c:if>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control" required
                       value="<c:out value='${form.email}'/>" placeholder="sahana@example.com" />
                <c:if test="${not empty fieldErrors.email}">
                    <div class="form-error"><c:out value="${fieldErrors.email}"/></div>
                </c:if>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number (Optional)</label>
                <input type="tel" id="phone" name="phone" class="form-control"
                       value="<c:out value='${form.phone}'/>" placeholder="+91 98765 43210" />
            </div>

            <div class="form-group">
                <label for="role">Account Type</label>
                <select id="role" name="role" class="form-control" required style="font-weight: 600;">
                    <option value="BUYER" ${form.role == 'BUYER' ? 'selected' : ''}>🛍️ Buyer (Browse, purchase, and review items)</option>
                    <option value="SELLER" ${form.role == 'SELLER' ? 'selected' : ''}>🏪 Seller (List products and fulfill customer orders)</option>
                </select>
                <small style="color: var(--text-secondary); font-size: 12px; margin-top: 6px; display: block;">
                    * Administrator accounts are pre-seeded per security specifications.
                </small>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control" required
                       placeholder="At least 6 characters" />
                <c:if test="${not empty fieldErrors.password}">
                    <div class="form-error"><c:out value="${fieldErrors.password}"/></div>
                </c:if>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required
                       placeholder="Re-type your password" />
                <c:if test="${not empty fieldErrors.confirmPassword}">
                    <div class="form-error"><c:out value="${fieldErrors.confirmPassword}"/></div>
                </c:if>
            </div>

            <button type="submit" class="btn btn-primary btn-lg" style="width: 100%; margin-top: 8px;">
                Create SahanaMart Account
            </button>
        </form>

        <p style="text-align: center; font-size: 14px; margin-top: 22px; color: var(--text-secondary);">
            Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 700;">Sign in here</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
