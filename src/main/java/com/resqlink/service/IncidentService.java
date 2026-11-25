package com.resqlink.service;

import com.resqlink.model.Incident;
import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentStatus;
import com.resqlink.model.enums.IncidentType;
import com.resqlink.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final NotificationService notificationService;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository, NotificationService notificationService) {
        this.incidentRepository = incidentRepository;
        this.notificationService = notificationService;
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAllByOrderByReportedAtDesc();
    }

    public List<Incident> getActiveIncidents() {
        return incidentRepository.findByStatus(IncidentStatus.ACTIVE);
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Optional<Incident> getIncidentByCode(String code) {
        return incidentRepository.findByIncidentCode(code);
    }

    @Transactional
    public Incident createIncident(String title, IncidentType type, IncidentSeverity severity, String description,
                                   String locationName, Double latitude, Double longitude, Double radiusKm,
                                   Integer affectedPeople, Integer casualties, String createdBy) {
        long count = incidentRepository.count() + 1;
        String code = String.format("INC-%04d", count);

        Incident incident = new Incident(code, title, type, severity, description, locationName, latitude, longitude,
                radiusKm != null ? radiusKm : 10.0,
                affectedPeople != null ? affectedPeople : 0,
                casualties != null ? casualties : 0);

        Incident saved = incidentRepository.save(incident);

        notificationService.logAndBroadcast(
                "INCIDENT",
                saved.getId(),
                "CREATED",
                createdBy,
                "New Incident Declared: " + saved.getTitle(),
                "Severity: " + saved.getSeverity().getLabel() + " at " + saved.getLocationName(),
                saved.getSeverity() == IncidentSeverity.CRITICAL ? "danger" : "warning"
        );

        return saved;
    }

    @Transactional
    public Incident updateIncidentStatus(Long id, IncidentStatus newStatus, String updatedBy) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found with ID: " + id));

        incident.setStatus(newStatus);
        incident.setUpdatedAt(LocalDateTime.now());
        Incident updated = incidentRepository.save(incident);

        notificationService.logAndBroadcast(
                "INCIDENT",
                updated.getId(),
                "STATUS_UPDATED",
                updatedBy,
                "Incident Status Changed: " + updated.getTitle(),
                "Status changed to " + newStatus.getLabel(),
                newStatus == IncidentStatus.RESOLVED ? "success" : "info"
        );

        return updated;
    }

    public long countActiveIncidents() {
        return incidentRepository.countByStatus(IncidentStatus.ACTIVE);
    }
}
