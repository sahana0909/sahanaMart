<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
</main>
<footer>
    <div class="container">
        <div class="footer-content">
            <div>
                <h3>🛍️ SahanaMart</h3>
                <p>
                    Premium multi-vendor e-commerce marketplace platform.
                    Engineered with Java Servlets, HikariCP, JDBC, and Apache Tomcat.
                </p>
                <p style="margin-top: 14px; color: #64748B; font-size: 13px;">
                    Fast, secure, reliable shopping with authentic products and verified sellers.
                </p>
            </div>
            <div>
                <h4>Quick Navigation</h4>
                <p><a href="${pageContext.request.contextPath}/products">Product Catalog</a></p>
                <p><a href="${pageContext.request.contextPath}/login">User Authentication</a></p>
                <p><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></p>
                <p><a href="${pageContext.request.contextPath}/api/v1/health" target="_blank">Health Check API ↗</a></p>
            </div>
            <div>
                <h4>Enterprise Highlights</h4>
                <p>• 100% Parameterized PreparedStatements</p>
                <p>• jBCrypt Password Salting & Hashing</p>
                <p>• Clean MVC Architecture (DAO / Service / Controller)</p>
                <p>• HikariCP High-Performance Connection Pool</p>
                <p>• AI-Powered Shopping Assistance</p>
            </div>
        </div>
        <div class="footer-bottom">
            &copy; 2026 SahanaMart E-Commerce Platform. All rights reserved. Designed with modern Blue, Violet & Teal theme.
        </div>
    </div>
</footer>
<jsp:include page="/WEB-INF/views/common/chatbot.jsp" />
</body>
</html>
