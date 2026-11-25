/**
 * ResQLink - Real-Time WebSocket Notifications Engine
 * Subscribes to /topic/alerts for live system updates
 */

document.addEventListener('DOMContentLoaded', () => {
    connectLiveAlerts();
});

function connectLiveAlerts() {
    try {
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        stompClient.debug = null; // Suppress verbose console logs

        stompClient.connect({}, (frame) => {
            console.log('Connected to ResQLink Live WebSocket Broker');

            stompClient.subscribe('/topic/alerts', (message) => {
                try {
                    const alertData = JSON.parse(message.body);
                    showLiveToast(alertData);
                } catch (e) {
                    console.error('Error parsing live notification:', e);
                }
            });
        }, (error) => {
            console.warn('WebSocket connection fallback/retry:', error);
            // Reconnect after 5 seconds if disconnected
            setTimeout(connectLiveAlerts, 5000);
        });
    } catch (e) {
        console.warn('SockJS / Stomp init failed:', e);
    }
}

function showLiveToast(data) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toastId = 'toast-' + Date.now();
    const bgClass = data.alertType === 'danger' ? 'bg-danger text-white' :
                    data.alertType === 'warning' ? 'bg-warning text-dark' :
                    data.alertType === 'success' ? 'bg-success text-white' : 'bg-primary text-white';

    const toastEl = document.createElement('div');
    toastEl.id = toastId;
    toastEl.className = `toast align-items-center ${bgClass} border-0 show shadow-lg mb-2`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');

    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <div class="d-flex align-items-center gap-2 mb-1">
                    <i class="bi bi-broadcast fs-6"></i>
                    <strong>${data.title || 'Emergency Alert'}</strong>
                </div>
                <div class="small">${data.message || ''}</div>
            </div>
            <button type="button" class="btn-close ${data.alertType === 'warning' ? '' : 'btn-close-white'} me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    container.appendChild(toastEl);

    // Auto-remove after 7 seconds
    setTimeout(() => {
        if (toastEl && toastEl.parentNode) {
            toastEl.classList.remove('show');
            setTimeout(() => toastEl.remove(), 500);
        }
    }, 7000);
}
