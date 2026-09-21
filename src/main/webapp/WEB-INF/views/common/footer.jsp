<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</main>
<footer>
    <div class="container">
        <div class="footer-content">
            <div>
                <h3 style="color: var(--white); margin-bottom: 10px;">🛍️ SahanaMart</h3>
                <p style="font-size: 14px; max-width: 320px; line-height: 1.5;">
                    Anna University R2025 Semester 3 Capstone Implementation.
                    Built with Java Servlets, JDBC, HikariCP, and Apache Tomcat.
                </p>
            </div>
            <div>
                <h4 style="color: var(--white); margin-bottom: 10px;">Quick Links</h4>
                <p><a href="${pageContext.request.contextPath}/products" style="font-size: 14px; color: var(--gray-300);">Product Catalog</a></p>
                <p><a href="${pageContext.request.contextPath}/login" style="font-size: 14px; color: var(--gray-300);">User Authentication</a></p>
                <p><a href="${pageContext.request.contextPath}/api/v1/health" style="font-size: 14px; color: var(--secondary);" target="_blank">System Health Check API</a></p>
            </div>
            <div>
                <h4 style="color: var(--white); margin-bottom: 10px;">Architectural Highlights</h4>
                <p style="font-size: 13px; color: var(--gray-300);">• 100% Parameterized PreparedStatements</p>
                <p style="font-size: 13px; color: var(--gray-300);">• jBCrypt Salting (12 Rounds)</p>
                <p style="font-size: 13px; color: var(--gray-300);">• Layered MVC Architecture (DAO / Service / Controller)</p>
                <p style="font-size: 13px; color: var(--gray-300);">• H2 Persistent File Database & Connection Pool</p>
            </div>
        </div>
        <div class="footer-bottom">
            &copy; 2026 SahanaMart Capstone Platform. Built for Anna University R2025 Evaluation.
        </div>
    </div>
</footer>
</body>
</html>
