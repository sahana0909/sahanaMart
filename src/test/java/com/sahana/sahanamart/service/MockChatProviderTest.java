package com.sahana.sahanamart.service;

import com.sahana.sahanamart.service.chat.MockChatProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MockChatProviderTest {

    private MockChatProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockChatProvider();
    }

    @Test
    @DisplayName("Provider should identify as available and return descriptive name")
    void testAvailabilityAndName() {
        assertThat(provider.isAvailable()).isTrue();
        assertThat(provider.getProviderName()).contains("FAQ");
    }

    @Test
    @DisplayName("Should answer order tracking query correctly")
    void testOrderTrackingQuery() {
        String reply = provider.generateReply("How do I track my order?", "session-1");
        assertThat(reply).contains("My Orders");
        assertThat(reply).contains("PENDING");
    }

    @Test
    @DisplayName("Should answer payment method inquiries")
    void testPaymentMethodsQuery() {
        String reply = provider.generateReply("What payment methods can I use?", "session-1");
        assertThat(reply).contains("UPI");
        assertThat(reply).contains("Card");
        assertThat(reply).contains("Cash on Delivery");
    }

    @Test
    @DisplayName("Should answer return policy questions")
    void testReturnPolicyQuery() {
        String reply = provider.generateReply("What is your refund and return policy?", "session-1");
        assertThat(reply).contains("7-day");
        assertThat(reply).contains("refund");
    }

    @Test
    @DisplayName("Should answer questions about becoming a seller")
    void testSellerOnboardingQuery() {
        String reply = provider.generateReply("How can I sell products on SahanaMart?", "session-1");
        assertThat(reply).contains("Seller");
        assertThat(reply).contains("Dashboard");
    }

    @Test
    @DisplayName("Should answer technology stack and capstone questions")
    void testTechStackQuery() {
        String reply = provider.generateReply("What is the tech stack of this capstone?", "session-1");
        assertThat(reply).contains("Java 17");
        assertThat(reply).contains("Tomcat");
        assertThat(reply).contains("Sahana D");
    }

    @Test
    @DisplayName("Should politely redirect out-of-scope queries")
    void testUnrelatedQueryFallback() {
        String reply = provider.generateReply("Can you write a poem about quantum physics?", "session-1");
        assertThat(reply).contains("SahanaMart's shopping assistant");
        assertThat(reply).contains("specialize");
    }
}
