// SahanaMart Client-Side Helpers & Chatbot Controller

document.addEventListener("DOMContentLoaded", () => {
    // 1. Auto-dismiss flash alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // 2. Preset login credential helper on login page
    window.fillCredentials = function(email, password) {
        const emailField = document.getElementById("loginEmail");
        const passField = document.getElementById("loginPassword");
        if (emailField && passField) {
            emailField.value = email;
            passField.value = password;
        }
    };
});

// ==========================================
// Chatbot Client Controller (Fetch API)
// ==========================================

function getContextPath() {
    if (typeof window.CONTEXT_PATH !== "undefined" && window.CONTEXT_PATH !== null) {
        return window.CONTEXT_PATH;
    }
    return window.location.pathname.startsWith("/sahanamart") ? "/sahanamart" : "";
}

function toggleChatbot() {
    const win = document.getElementById("chatbotWindow");
    if (!win) return;

    if (win.style.display === "none" || win.style.display === "") {
        win.style.display = "flex";
        const input = document.getElementById("chatbotInput");
        if (input) {
            setTimeout(() => input.focus(), 150);
        }
        scrollChatToBottom();
    } else {
        win.style.display = "none";
    }
}

function sendSuggestion(text) {
    const input = document.getElementById("chatbotInput");
    if (input) {
        input.value = text;
        const form = document.getElementById("chatbotForm");
        if (form) {
            const fakeEvent = new Event("submit", { cancelable: true });
            form.dispatchEvent(fakeEvent);
        }
    }
}

function handleChatSubmit(event) {
    if (event) {
        event.preventDefault();
    }

    const input = document.getElementById("chatbotInput");
    const sendBtn = document.getElementById("chatbotSendBtn");
    const typingIndicator = document.getElementById("chatbotTyping");
    if (!input) return;

    const message = input.value.trim();
    if (!message) return;

    if (message.length > 500) {
        alert("Your message is too long (maximum 500 characters). Please shorten it.");
        return;
    }

    // Append user message safely
    appendChatMessage("user", message);
    input.value = "";
    if (sendBtn) sendBtn.disabled = true;
    if (typingIndicator) typingIndicator.style.display = "flex";
    scrollChatToBottom();

    // Call REST endpoint /api/chat via Fetch API
    const endpoint = getContextPath() + "/api/chat";

    fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json;charset=UTF-8",
            "Accept": "application/json"
        },
        body: JSON.stringify({ message: message })
    })
    .then(async response => {
        const isJson = response.headers.get("content-type")?.includes("application/json");
        const data = isJson ? await response.json() : null;

        if (response.ok && data && data.success) {
            appendChatMessage("bot", data.reply, data.provider);
        } else if (response.status === 429) {
            appendChatMessage("bot", "⚠️ " + (data?.error || "You are sending messages too quickly. Please wait a minute."), "System");
        } else if (data && data.error) {
            appendChatMessage("bot", "⚠️ " + data.error, "System");
        } else {
            appendChatMessage("bot", "I am having trouble reaching the assistant. Please try asking again in a moment.", "Fallback");
        }
    })
    .catch(err => {
        console.error("Chatbot Fetch error:", err);
        appendChatMessage("bot", "Unable to connect to the assistant. Please check your internet connection and try again.", "Offline");
    })
    .finally(() => {
        if (sendBtn) sendBtn.disabled = false;
        if (typingIndicator) typingIndicator.style.display = "none";
        scrollChatToBottom();
    });
}

function appendChatMessage(sender, text, provider) {
    const container = document.getElementById("chatbotMessages");
    if (!container) return;

    const bubble = document.createElement("div");
    bubble.className = "chat-bubble " + (sender === "user" ? "user-bubble" : "bot-bubble");

    const textPara = document.createElement("p");
    textPara.textContent = text; // Safe textContent to prevent XSS
    bubble.appendChild(textPara);

    if (provider && sender === "bot") {
        const provMeta = document.createElement("span");
        provMeta.className = "chat-provider-tag";
        provMeta.textContent = provider;
        bubble.appendChild(provMeta);
    }

    container.appendChild(bubble);
    scrollChatToBottom();
}

function scrollChatToBottom() {
    const container = document.getElementById("chatbotMessages");
    if (container) {
        container.scrollTop = container.scrollHeight;
    }
}
