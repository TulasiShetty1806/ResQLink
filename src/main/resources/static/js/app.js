/**
 * ResQLink - Application General Scripts
 */

document.addEventListener('DOMContentLoaded', () => {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

/**
 * Helper to autofill and submit login form for demo evaluation
 */
function quickLogin(username, password) {
    const userField = document.getElementById('username');
    const passField = document.getElementById('password');
    const loginForm = document.getElementById('loginForm');

    if (userField && passField && loginForm) {
        userField.value = username;
        passField.value = password;
        loginForm.submit();
    }
}
