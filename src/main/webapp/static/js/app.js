// SahanaMart Client-Side Helpers

document.addEventListener("DOMContentLoaded", () => {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // Preset login helpers on login page
    window.fillCredentials = function(email, password) {
        const emailField = document.getElementById("loginEmail");
        const passField = document.getElementById("loginPassword");
        if (emailField && passField) {
            emailField.value = email;
            passField.value = password;
        }
    };
});
