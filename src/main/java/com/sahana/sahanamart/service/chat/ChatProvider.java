package com.sahana.sahanamart.service.chat;

/**
 * Interface representing an AI or Rule-Based Chat Provider for SahanaMart.
 */
public interface ChatProvider {

    /**
     * Name of the provider (e.g., "Mock FAQ Provider", "Google Gemini AI").
     */
    String getProviderName();

    /**
     * Generate a conversational reply for the user query.
     *
     * @param userMessage raw question from the user
     * @param sessionId   current user HTTP session identifier
     * @return response text suitable for display
     */
    String generateReply(String userMessage, String sessionId);

    /**
     * Checks if this provider is currently available and ready to serve requests.
     */
    boolean isAvailable();
}
