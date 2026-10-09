package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.controller.api.ChatServlet;
import com.sahana.sahanamart.service.chat.ChatService;
import com.sahana.sahanamart.service.chat.MockChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    private ChatServlet servlet;
    private ChatService chatService;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ChatServlet();
        chatService = new ChatService(new MockChatProvider());

        org.mockito.Mockito.lenient().when(servletConfig.getServletContext()).thenReturn(servletContext);
        org.mockito.Mockito.lenient().when(servletContext.getAttribute("chatService")).thenReturn(chatService);

        servlet.init(servletConfig);

        responseWriter = new StringWriter();
        org.mockito.Mockito.lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("doGet should return welcome message and suggested FAQ questions")
    void testDoGetWelcome() throws Exception {
        servlet.doGet(request, response);

        verify(response).setContentType("application/json;charset=UTF-8");
        String output = responseWriter.toString();
        assertThat(output).contains("welcome");
        assertThat(output).contains("suggestions");
        assertThat(output).contains("Track my order");
    }

    @Test
    @DisplayName("doPost with valid question should return HTTP 200 and answer")
    void testDoPostValidQuestion() throws Exception {
        when(request.getParameter("message")).thenReturn("What payment methods are supported?");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("test-sess-1");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String output = responseWriter.toString();
        assertThat(output).contains("\"success\":true");
        assertThat(output).contains("UPI");
    }

    @Test
    @DisplayName("doPost with empty question should return HTTP 400 and validation error")
    void testDoPostEmptyQuestion() throws Exception {
        when(request.getParameter("message")).thenReturn("   ");
        when(request.getSession(true)).thenReturn(session);
        when(session.getId()).thenReturn("test-sess-1");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String output = responseWriter.toString();
        assertThat(output).contains("\"success\":false");
        assertThat(output).contains("cannot be empty");
    }
}
