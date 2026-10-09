package com.sahana.sahanamart.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.service.chat.ChatService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for SahanaMart AI & Smart FAQ Chatbot.
 * Endpoint: POST /api/chat
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/api/chat"})
public class ChatServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ChatService getChatService() {
        ChatService service = (ChatService) getServletContext().getAttribute("chatService");
        if (service == null) {
            service = new ChatService();
            getServletContext().setAttribute("chatService", service);
        }
        return service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> welcome = Map.of(
                "success", true,
                "welcome", "Hello! Welcome to SahanaMart shopping assistant. How may I help you?",
                "suggestions", java.util.List.of(
                        "Track my order",
                        "What payment methods are supported?",
                        "What is your return policy?",
                        "How can I become a seller?",
                        "How to rate and review products?"
                )
        );
        resp.getWriter().write(objectMapper.writeValueAsString(welcome));
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String userMessage = null;
        String contentType = req.getContentType();

        // Check if request is JSON
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            try (BufferedReader reader = req.getReader()) {
                JsonNode root = objectMapper.readTree(reader);
                if (root != null && root.has("message")) {
                    userMessage = root.get("message").asText();
                }
            } catch (Exception ignored) {}
        }

        // Fallback to form parameter if not in JSON body
        if (userMessage == null) {
            userMessage = req.getParameter("message");
        }

        HttpSession session = req.getSession(true);
        String sessionId = session.getId();

        PrintWriter out = resp.getWriter();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            ChatService.ChatResult result = getChatService().processMessage(userMessage, sessionId);
            resp.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("success", true);
            responseMap.put("reply", result.getReply());
            responseMap.put("provider", result.getProvider());
        } catch (ValidationException e) {
            if ("rateLimit".equals(e.getFieldErrors().keySet().stream().findFirst().orElse(""))) {
                resp.setStatus(429); // Too Many Requests
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            responseMap.put("success", false);
            responseMap.put("error", e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("success", false);
            responseMap.put("error", "The chat service encountered a temporary error. Please try again in a moment.");
        }

        out.write(objectMapper.writeValueAsString(responseMap));
        out.flush();
    }
}
