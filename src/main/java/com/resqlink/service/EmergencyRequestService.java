package com.resqlink.service;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.RequestUrgency;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.repository.EmergencyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository requestRepository;
    private final NotificationService notificationService;

    @Autowired
    public EmergencyRequestService(EmergencyRequestRepository requestRepository, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
    }

    public List<EmergencyRequest> getAllRequests() {
        return requestRepository.findAllByOrderByPriorityScoreDesc();
    }

    public List<EmergencyRequest> getPendingTriageRequests() {
        return requestRepository.findUnfulfilledRequestsPrioritized();
    }

    public Optional<EmergencyRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public Optional<EmergencyRequest> getRequestByTrackingCode(String trackingCode) {
        return requestRepository.findByTrackingCode(trackingCode);
    }

    /**
     * Algorithmic Triage Engine: Calculates dynamic multi-variable priority score (0 - 100).
     */
    public double calculatePriorityScore(EmergencyRequest request) {
        double score = 0.0;

        // 1. Request Urgency component (Max 40 points)
        if (request.getUrgency() != null) {
            score += request.getUrgency().getWeight();
        }

        // 2. Incident Severity component (Max 30 points)
        if (request.getIncident() != null && request.getIncident().getSeverity() != null) {
            score += request.getIncident().getSeverity().getWeight() * 0.75;
        }

        // 3. Human vulnerability factor (Max 20 points)
        int people = (request.getAffectedPeopleCount() != null) ? request.getAffectedPeopleCount() : 1;
        double peopleWeight = Math.min(15.0, people * 1.5);
        score += peopleWeight;

        if (request.isHasInjuredOrInfants()) {
            score += 10.0;
        }

        // 4. Time wait escalation factor (Up to 15 points: +1.5 pts per 15 minutes of unaddressed wait)
        if (request.getRequestedAt() != null) {
            long minutesWaiting = Duration.between(request.getRequestedAt(), LocalDateTime.now()).toMinutes();
            double waitFactor = Math.min(15.0, (minutesWaiting / 15.0) * 1.5);
            score += waitFactor;
        }

        // Clamp between 5.0 and 100.0
        score = Math.max(5.0, Math.min(100.0, score));
        return Math.round(score * 10.0) / 10.0;
    }

    @Transactional
    public EmergencyRequest submitRequest(Incident incident, ResourceCategory category, String itemName,
                                          Integer requestedQuantity, String unit, RequestUrgency urgency,
                                          String requesterName, String requesterPhone, String requesterAddress,
                                          Double deliveryLatitude, Double deliveryLongitude,
                                          Integer affectedPeopleCount, boolean hasInjuredOrInfants,
                                          String specialInstructions) {

        String trackingCode = "REQ-" + (int)(Math.random() * 9000 + 1000) + "-" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();

        EmergencyRequest request = new EmergencyRequest(
                trackingCode, incident, category, itemName, requestedQuantity, unit, urgency,
                requesterName, requesterPhone, requesterAddress, deliveryLatitude, deliveryLongitude,
                affectedPeopleCount, hasInjuredOrInfants, specialInstructions
        );

        // Run prioritization engine
        double score = calculatePriorityScore(request);
        request.setPriorityScore(score);
        request.setStatus(RequestStatus.PRIORITIZED);

        EmergencyRequest saved = requestRepository.save(request);

        notificationService.logAndBroadcast(
                "REQUEST",
                saved.getId(),
                "SUBMITTED",
                requesterName,
                "Emergency Aid Request #" + saved.getTrackingCode(),
                "Needs " + saved.getRequestedQuantity() + " " + saved.getUnit() + " of " + saved.getItemName() +
                        " (Priority Score: " + saved.getPriorityScore() + ")",
                saved.getPriorityScore() >= 70 ? "danger" : "warning"
        );

        return saved;
    }

    @Transactional
    public void recalculateAllPriorities() {
        List<EmergencyRequest> activeRequests = requestRepository.findUnfulfilledRequestsPrioritized();
        for (EmergencyRequest req : activeRequests) {
            double newScore = calculatePriorityScore(req);
            req.setPriorityScore(newScore);
            requestRepository.save(req);
        }
    }

    @Transactional
    public EmergencyRequest updateStatus(Long requestId, RequestStatus newStatus, String updatedBy) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));
        request.setStatus(newStatus);
        request.setUpdatedAt(LocalDateTime.now());
        EmergencyRequest updated = requestRepository.save(request);

        notificationService.logAndBroadcast(
                "REQUEST",
                updated.getId(),
                "STATUS_CHANGE",
                updatedBy,
                "Request #" + updated.getTrackingCode() + " Status Updated",
                "New Status: " + newStatus.getLabel(),
                "info"
        );
        return updated;
    }

    public long countActiveRequests() {
        return requestRepository.countActiveRequests();
    }
}
