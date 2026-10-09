package com.sahana.sahanamart.service;

import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.service.chat.ChatService;
import com.sahana.sahanamart.service.chat.MockChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChatServiceTest {

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(new MockChatProvider());
    }

    @Test
    @DisplayName("Should process valid question and return answer with provider name")
    void testProcessValidMessage() {
        ChatService.ChatResult result = chatService.processMessage("Where is my order?", "session-abc");
        assertThat(result).isNotNull();
        assertThat(result.getReply()).isNotEmpty();
        assertThat(result.getProvider()).contains("FAQ");
    }

    @Test
    @DisplayName("Should reject empty or whitespace message")
    void testRejectEmptyMessage() {
        assertThatThrownBy(() -> chatService.processMessage("", "session-abc"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("cannot be empty");

        assertThatThrownBy(() -> chatService.processMessage("    ", "session-abc"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("Should reject message exceeding 500 characters")
    void testRejectLongMessage() {
        String longMsg = "A".repeat(501);
        assertThatThrownBy(() -> chatService.processMessage(longMsg, "session-abc"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("maximum limit of 500 characters");
    }

    @Test
    @DisplayName("Should enforce rate limit of 10 messages per minute per session")
    void testRateLimiting() {
        String sessionId = "rate-limit-session";

        // First 10 messages should succeed
        for (int i = 0; i < 10; i++) {
            ChatService.ChatResult result = chatService.processMessage("Question #" + i, sessionId);
            assertThat(result).isNotNull();
        }

        // 11th message must be throttled
        assertThatThrownBy(() -> chatService.processMessage("Question #11", sessionId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Limit is 10 messages per minute");

        // Different session should not be throttled
        ChatService.ChatResult otherSessionResult = chatService.processMessage("Hello from other session", "session-other");
        assertThat(otherSessionResult).isNotNull();
    }

    @Test
    @DisplayName("Should return cached response for identical questions in same session")
    void testSessionCaching() {
        String sessionId = "cache-session";
        String question = "What is the return policy?";

        ChatService.ChatResult first = chatService.processMessage(question, sessionId);
        ChatService.ChatResult second = chatService.processMessage(question, sessionId);

        assertThat(first.getReply()).isEqualTo(second.getReply());
        assertThat(second.getProvider()).contains("Cached");
    }
}
