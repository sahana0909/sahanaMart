package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.UserLoginDTO;
import com.sahana.sahanamart.dto.UserRegisterDTO;
import com.sahana.sahanamart.exception.AuthenticationException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Role;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;
import com.sahana.sahanamart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserService getUserService() {
        return (UserService) getServletContext().getAttribute("userService");
    }

    private CartService getCartService() {
        return (CartService) getServletContext().getAttribute("cartService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/login":
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String returnUrl = req.getParameter("returnUrl");

        UserLoginDTO loginDTO = new UserLoginDTO(email, password);
        try {
            User user = getUserService().login(loginDTO);

            // Session Fixation Protection: invalidate existing session & create new session
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(1800); // 30 minutes explicit timeout
            newSession.setAttribute("currentUser", user);

            // Set cart count in session
            if (user.getRole() == Role.BUYER) {
                int cartCount = getCartService().getCartItemCount(user.getId());
                newSession.setAttribute("cartCount", cartCount);
            }

            if (returnUrl != null && !returnUrl.trim().isEmpty() && !returnUrl.contains("/login")) {
                resp.sendRedirect(returnUrl);
                return;
            }

            // Redirect based on role
            if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (user.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }
        } catch (AuthenticationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("returnUrl", returnUrl);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserRegisterDTO dto = new UserRegisterDTO(
            req.getParameter("email"),
            req.getParameter("password"),
            req.getParameter("confirmPassword"),
            req.getParameter("fullName"),
            req.getParameter("role"),
            req.getParameter("phone")
        );

        try {
            getUserService().register(dto);
            resp.sendRedirect(req.getContextPath() + "/login?success=Account registered successfully! Please log in.");
        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("form", dto);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?success=You have been successfully logged out.");
    }
}
