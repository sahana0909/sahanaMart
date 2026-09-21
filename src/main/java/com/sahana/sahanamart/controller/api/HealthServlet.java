package com.sahana.sahanamart.controller.api;

import com.sahana.sahanamart.util.DatabaseUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HealthServlet", urlPatterns = {"/api/v1/health"})
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        boolean dbHealthy = DatabaseUtil.isHealthy();

        if (dbHealthy) {
            resp.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = resp.getWriter()) {
                out.print("{\"status\":\"UP\",\"db\":\"UP\"}");
                out.flush();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            try (PrintWriter out = resp.getWriter()) {
                out.print("{\"status\":\"DOWN\",\"db\":\"DOWN\"}");
                out.flush();
            }
        }
    }
}
