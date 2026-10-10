<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Floating Chatbot Widget (AI & Smart Shopping Assistant) -->
<div id="sahanaChatbotContainer" class="chatbot-wrapper">
    <!-- Chat Trigger Bubble Button -->
    <button id="chatbotToggleBtn" class="chatbot-toggle-btn" type="button" aria-label="Open SahanaMart Assistant" onclick="toggleChatbot()">
        <span class="chat-icon">💬</span>
        <span class="chat-btn-text">Chat with AI</span>
    </button>

    <!-- Chat Modal Window -->
    <div id="chatbotWindow" class="chatbot-window" style="display: none;">
        <!-- Header -->
        <div class="chatbot-header">
            <div class="chatbot-header-info">
                <div class="chatbot-avatar">🛍️</div>
                <div>
                    <h4 class="chatbot-title">SahanaMart AI Assistant</h4>
                    <span class="chatbot-status"><span class="status-dot"></span> Online • Instant Answers</span>
                </div>
            </div>
            <button class="chatbot-close-btn" type="button" onclick="toggleChatbot()" aria-label="Close Chat">✕</button>
        </div>

        <!-- Chat Conversation Messages -->
        <div id="chatbotMessages" class="chatbot-messages">
            <!-- Welcome Bot Message -->
            <div class="chat-bubble bot-bubble">
                <p>Hello! 👋 I'm your <strong>SahanaMart Shopping Assistant</strong>. Ask me anything about tracking orders, payments, products, returns, or selling on our platform!</p>
            </div>

            <!-- Quick Suggestion Chips -->
            <div class="chat-suggestions" id="chatSuggestions">
                <span class="suggestion-chip" onclick="sendSuggestion('How do I track my order?')">📦 Track order</span>
                <span class="suggestion-chip" onclick="sendSuggestion('What payment methods are supported?')">💳 Payment methods</span>
                <span class="suggestion-chip" onclick="sendSuggestion('What is your return policy?')">↩️ 7-Day Returns</span>
                <span class="suggestion-chip" onclick="sendSuggestion('How can I become a seller?')">🏪 Sell on SahanaMart</span>
                <span class="suggestion-chip" onclick="sendSuggestion('How to rate and review products?')">⭐ Rate products</span>
            </div>
        </div>

        <!-- Typing Indicator -->
        <div id="chatbotTyping" class="chat-typing-indicator" style="display: none;">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span style="font-size: 12px; color: var(--text-secondary); margin-left: 6px;">Assistant is typing...</span>
        </div>

        <!-- Input Footer -->
        <form id="chatbotForm" class="chatbot-input-bar" onsubmit="handleChatSubmit(event)">
            <input type="text" id="chatbotInput" class="chatbot-input" placeholder="Ask a question (e.g. return policy)..."
                   maxlength="500" autocomplete="off" required />
            <button type="submit" id="chatbotSendBtn" class="chatbot-send-btn" title="Send Question">
                ➤
            </button>
        </form>
    </div>
</div>
