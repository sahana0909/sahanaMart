package com.sahana.sahanamart.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Optional Google Gemini AI Chat Provider.
 * Integrates via Google Generative Language REST API when GEMINI_API_KEY is configured.
 * Seamlessly delegates to MockChatProvider if unconfigured, rate-limited, or offline.
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger LOGGER = Logger.getLogger(GeminiChatProvider.class.getName());
    private static final String GEMINI_MODEL = "gemini-1.5-flash";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MockChatProvider fallbackProvider;

    public GeminiChatProvider(MockChatProvider fallback) {
        this.fallbackProvider = fallback != null ? fallback : new MockChatProvider();
        this.apiKey = resolveApiKey();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private String resolveApiKey() {
        // 1. Environment variable
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        // 2. System property
        key = System.getProperty("gemini.api.key");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        // 3. config.properties classpath file
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String propKey = props.getProperty("gemini.api.key");
                if (propKey != null && !propKey.trim().isEmpty()) {
                    return propKey.trim();
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    @Override
    public String getProviderName() {
        return (isAvailable()) ? "Google Gemini AI (" + GEMINI_MODEL + ")" : fallbackProvider.getProviderName();
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String generateReply(String userMessage, String sessionId) {
        if (!isAvailable()) {
            return fallbackProvider.generateReply(userMessage, sessionId);
        }

        try {
            String promptText = "You are the virtual shopping assistant for SahanaMart, an e-commerce platform developed by Sahana D for Anna University R2025. "
                    + "Help the buyer or seller with questions about products, cart, checkout, payments (UPI/Card/COD), order tracking, 7-day returns, and seller onboarding. "
                    + "Keep replies helpful, concise (maximum 120 words), and friendly. If the user asks something completely unrelated to shopping or SahanaMart, politely decline. "
                    + "\n\nUser Question: " + userMessage;

            String requestBody = objectMapper.writeValueAsString(
                    java.util.Map.of("contents", java.util.List.of(
                            java.util.Map.of("parts", java.util.List.of(
                                    java.util.Map.of("text", promptText)
                            ))
                    ))
            );

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(TIMEOUT)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode candidateText = root.at("/candidates/0/content/parts/0/text");
                if (!candidateText.isMissingNode() && !candidateText.asText().isBlank()) {
                    return candidateText.asText().trim();
                }
            } else {
                LOGGER.warning("Gemini API call returned status: " + response.statusCode() + " - Falling back to Mock FAQ.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Gemini API request failed or timed out: " + e.getMessage() + ". Using fallback FAQ.");
        }

        return fallbackProvider.generateReply(userMessage, sessionId);
    }
}
