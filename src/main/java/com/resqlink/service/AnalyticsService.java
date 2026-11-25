package com.resqlink.service;

import com.resqlink.model.AuditLog;
import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentStatus;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final IncidentRepository incidentRepository;
    private final EmergencyRequestRepository requestRepository;
    private final ResourceItemRepository resourceItemRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final TransportMissionRepository transportRepository;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AnalyticsService(IncidentRepository incidentRepository,
                            EmergencyRequestRepository requestRepository,
                            ResourceItemRepository resourceItemRepository,
                            ResourceAllocationRepository allocationRepository,
                            TransportMissionRepository transportRepository,
                            AuditLogRepository auditLogRepository) {
        this.incidentRepository = incidentRepository;
        this.requestRepository = requestRepository;
        this.resourceItemRepository = resourceItemRepository;
        this.allocationRepository = allocationRepository;
        this.transportRepository = transportRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Core Counts
        long totalIncidents = incidentRepository.count();
        long activeIncidents = incidentRepository.countByStatus(IncidentStatus.ACTIVE);
        long totalRequests = requestRepository.count();
        long activeRequests = requestRepository.countActiveRequests();
        long deliveredRequests = requestRepository.countByStatus(RequestStatus.DELIVERED);
        long pendingTriage = requestRepository.countByStatus(RequestStatus.PENDING_TRIAGE) +
                requestRepository.countByStatus(RequestStatus.PRIORITIZED);
        long allocatedRequests = requestRepository.countByStatus(RequestStatus.ALLOCATED);
        long inTransitRequests = requestRepository.countByStatus(RequestStatus.DISPATCHED);

        Long totalStock = resourceItemRepository.sumTotalAvailableSupplies();
        Long totalPeopleAffected = requestRepository.sumTotalAffectedPeople();

        metrics.put("totalIncidents", totalIncidents);
        metrics.put("activeIncidents", activeIncidents);
        metrics.put("totalRequests", totalRequests);
        metrics.put("activeRequests", activeRequests);
        metrics.put("deliveredRequests", deliveredRequests);
        metrics.put("pendingTriage", pendingTriage);
        metrics.put("allocatedRequests", allocatedRequests);
        metrics.put("inTransitRequests", inTransitRequests);
        metrics.put("totalStock", totalStock != null ? totalStock : 0L);
        metrics.put("totalPeopleAffected", totalPeopleAffected != null ? totalPeopleAffected : 0L);

        // Requests by Category
        Map<String, Long> categoryCounts = new HashMap<>();
        for (ResourceCategory cat : ResourceCategory.values()) {
            long count = requestRepository.findByCategory(cat).size();
            categoryCounts.put(cat.getLabel(), count);
        }
        metrics.put("categoryCounts", categoryCounts);

        // Incidents by Severity
        Map<String, Long> severityCounts = new HashMap<>();
        for (IncidentSeverity sev : IncidentSeverity.values()) {
            long count = incidentRepository.findBySeverity(sev).size();
            severityCounts.put(sev.name(), count);
        }
        metrics.put("severityCounts", severityCounts);

        return metrics;
    }

    public List<AuditLog> getRecentActivity() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
