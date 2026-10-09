package com.sahana.sahanamart.service.chat;

import com.sahana.sahanamart.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Service orchestrating Chatbot communication, session caching, and sliding-window rate limiting.
 */
public class ChatService {

    private static final Logger LOGGER = Logger.getLogger(ChatService.class.getName());
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int RATE_LIMIT_MAX_PER_MINUTE = 10;
    private static final long RATE_LIMIT_WINDOW_MS = 60_000L;

    private final ChatProvider primaryProvider;
    private final MockChatProvider fallbackProvider;

    // Session-level sliding rate limit: sessionId -> List of request timestamps
    private final Map<String, List<Long>> sessionRequestTimestamps = new ConcurrentHashMap<>();

    // Session-level identical question cache: sessionId -> (normalized question -> answer)
    private final Map<String, Map<String, String>> sessionCache = new ConcurrentHashMap<>();

    public ChatService() {
        this.fallbackProvider = new MockChatProvider();
        this.primaryProvider = new GeminiChatProvider(this.fallbackProvider);
    }

    public ChatService(ChatProvider customProvider) {
        this.fallbackProvider = new MockChatProvider();
        this.primaryProvider = customProvider != null ? customProvider : this.fallbackProvider;
    }

    /**
     * Process an incoming user message safely.
     *
     * @param rawMessage the user prompt
     * @param sessionId  user session ID for caching and rate limiting
     * @return ChatResult containing reply and provider name
     * @throws ValidationException if message is empty, exceeds length, or rate limit exceeded
     */
    public ChatResult processMessage(String rawMessage, String sessionId) {
        String sessionKey = (sessionId != null && !sessionId.isBlank()) ? sessionId : "anonymous";

        // 1. Validate empty message
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            throw new ValidationException("message", "Message cannot be empty. Please type a question.");
        }

        String trimmed = rawMessage.trim();

        // 2. Validate maximum length
        if (trimmed.length() > MAX_MESSAGE_LENGTH) {
            throw new ValidationException("message",
                    "Message exceeds the maximum limit of " + MAX_MESSAGE_LENGTH + " characters.");
        }

        // 3. Sliding-window rate limit (10 requests per minute per session)
        checkRateLimit(sessionKey);

        // 4. Session-level question cache check
        String normalizedKey = trimmed.toLowerCase(Locale.ROOT);
        Map<String, String> cacheForSession = sessionCache.computeIfAbsent(sessionKey, k -> new ConcurrentHashMap<>());
        if (cacheForSession.containsKey(normalizedKey)) {
            LOGGER.info("Returning cached response for question in session: " + sessionKey);
            return new ChatResult(cacheForSession.get(normalizedKey), primaryProvider.getProviderName() + " (Cached)");
        }

        // 5. Generate reply via primary provider (or fallback)
        String reply;
        String providerName;
        try {
            reply = primaryProvider.generateReply(trimmed, sessionKey);
            providerName = primaryProvider.getProviderName();
        } catch (Exception e) {
            LOGGER.warning("Chat provider encountered error: " + e.getMessage() + ". Using fallback.");
            reply = fallbackProvider.generateReply(trimmed, sessionKey);
            providerName = fallbackProvider.getProviderName();
        }

        // 6. Cache response for identical questions in this session
        cacheForSession.put(normalizedKey, reply);

        return new ChatResult(reply, providerName);
    }

    private synchronized void checkRateLimit(String sessionKey) {
        long now = System.currentTimeMillis();
        List<Long> timestamps = sessionRequestTimestamps.computeIfAbsent(sessionKey, k -> Collections.synchronizedList(new ArrayList<>()));

        // Prune older timestamps outside 1-minute sliding window
        timestamps.removeIf(t -> (now - t) > RATE_LIMIT_WINDOW_MS);

        if (timestamps.size() >= RATE_LIMIT_MAX_PER_MINUTE) {
            throw new ValidationException("rateLimit",
                    "You are sending messages too quickly. Limit is " + RATE_LIMIT_MAX_PER_MINUTE + " messages per minute. Please wait a moment.");
        }

        timestamps.add(now);
    }

    /**
     * Clear session caches when a user logs out.
     */
    public void clearSession(String sessionId) {
        if (sessionId != null) {
            sessionRequestTimestamps.remove(sessionId);
            sessionCache.remove(sessionId);
        }
    }

    public static class ChatResult {
        private final String reply;
        private final String provider;

        public ChatResult(String reply, String provider) {
            this.reply = reply;
            this.provider = provider;
        }

        public String getReply() {
            return reply;
        }

        public String getProvider() {
            return provider;
        }
    }
}
