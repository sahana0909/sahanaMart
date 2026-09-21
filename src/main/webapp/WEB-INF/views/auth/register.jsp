<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Create Account | SahanaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div style="max-width: 480px; margin: 40px auto;">
    <div class="card">
        <h2 style="font-size: 24px; font-weight: 800; margin-bottom: 8px; text-align: center;">Join SahanaMart</h2>
        <p style="color: var(--gray-500); font-size: 14px; text-align: center; margin-bottom: 24px;">Register as a Buyer or Seller</p>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" class="form-control" required
                       value="<c:out value='${form.fullName}'/>" placeholder="Sahana Devi" />
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
                       value="<c:out value='${form.phone}'/>" placeholder="+91 9876543210" />
            </div>

            <div class="form-group">
                <label for="role">Account Role</label>
                <select id="role" name="role" class="form-control" required>
                    <option value="BUYER" ${form.role == 'BUYER' ? 'selected' : ''}>Buyer (Shop and review items)</option>
                    <option value="SELLER" ${form.role == 'SELLER' ? 'selected' : ''}>Seller (List and sell products)</option>
                </select>
                <small style="color: var(--gray-500); font-size: 12px; margin-top: 4px; display: block;">
                    * Admin accounts are strictly pre-seeded per capstone security specification.
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
                       placeholder="Repeat password" />
                <c:if test="${not empty fieldErrors.confirmPassword}">
                    <div class="form-error"><c:out value="${fieldErrors.confirmPassword}"/></div>
                </c:if>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Create Account</button>
        </form>

        <p style="text-align: center; font-size: 14px; margin-top: 20px; color: var(--gray-500);">
            Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign in</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
