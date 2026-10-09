package com.sahana.sahanamart.filter;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {
    "/buyer/*", "/seller/*", "/admin/*",
    "/cart", "/cart/*", "/checkout", "/checkout/*", "/reviews/*"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (currentUser == null) {
            // Not logged in
            if (path.startsWith("/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json;charset=UTF-8");
                JsonUtil.writeJson(resp.getWriter(), ApiResponse.error("Authentication required"));
                return;
            } else {
                resp.sendRedirect(contextPath + "/login?error=Please log in to continue&returnUrl=" + req.getRequestURI());
                return;
            }
        }

        Role role = currentUser.getRole();

        // Check Seller routes
        if (path.startsWith("/seller/")) {
            if (role != Role.SELLER && role != Role.ADMIN) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Seller role required");
                return;
            }
        }

        // Check Admin routes
        if (path.startsWith("/admin/")) {
            if (role != Role.ADMIN) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Admin role required");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
