package com.resqlink.service;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.TransportMission;
import com.resqlink.model.enums.AllocationStatus;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.TransportMode;
import com.resqlink.model.enums.TransportStatus;
import com.resqlink.repository.EmergencyRequestRepository;
import com.resqlink.repository.ResourceAllocationRepository;
import com.resqlink.repository.ResourceItemRepository;
import com.resqlink.repository.TransportMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransportService {

    private final TransportMissionRepository transportRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final EmergencyRequestRepository requestRepository;
    private final ResourceItemRepository resourceItemRepository;
    private final DistanceCalculator distanceCalculator;
    private final NotificationService notificationService;

    @Autowired
    public TransportService(TransportMissionRepository transportRepository,
                            ResourceAllocationRepository allocationRepository,
                            EmergencyRequestRepository requestRepository,
                            ResourceItemRepository resourceItemRepository,
                            DistanceCalculator distanceCalculator,
                            NotificationService notificationService) {
        this.transportRepository = transportRepository;
        this.allocationRepository = allocationRepository;
        this.requestRepository = requestRepository;
        this.resourceItemRepository = resourceItemRepository;
        this.distanceCalculator = distanceCalculator;
        this.notificationService = notificationService;
    }

    public List<TransportMission> getAllMissions() {
        return transportRepository.findAllByOrderByDispatchTimeDesc();
    }

    public Optional<TransportMission> getMissionById(Long id) {
        return transportRepository.findById(id);
    }

    public Optional<TransportMission> getMissionByTrackingNumber(String trackingNumber) {
        return transportRepository.findByTrackingNumber(trackingNumber);
    }

    public Optional<TransportMission> getMissionByRequest(EmergencyRequest request) {
        return transportRepository.findByEmergencyRequest(request);
    }

    @Transactional
    public TransportMission dispatchTransport(Long allocationId, TransportMode mode, String vehicleNumber,
                                              String driverName, String driverPhone, String routeNotes, String dispatchedBy) {

        ResourceAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found: " + allocationId));

        EmergencyRequest request = allocation.getEmergencyRequest();
        ResourceItem item = allocation.getResourceItem();

        double distanceKm = distanceCalculator.calculateDistanceKm(
                item.getDepotLatitude(), item.getDepotLongitude(),
                request.getDeliveryLatitude(), request.getDeliveryLongitude()
        );

        int durationMin = distanceCalculator.estimateDurationMinutes(distanceKm, mode);

        long count = transportRepository.count() + 1;
        String trackingNumber = String.format("TRK-%04d", count);

        TransportMission mission = new TransportMission(
                trackingNumber, request, allocation, mode, vehicleNumber, driverName, driverPhone,
                distanceKm, durationMin, routeNotes
        );

        TransportMission saved = transportRepository.save(mission);

        // Update Allocation & Request status to In-Transit / Dispatched
        allocation.setStatus(AllocationStatus.DISPATCHED);
        allocationRepository.save(allocation);

        request.setStatus(RequestStatus.DISPATCHED);
        requestRepository.save(request);

        notificationService.logAndBroadcast(
                "TRANSPORT",
                saved.getId(),
                "DISPATCHED",
                dispatchedBy,
                "Mission Dispatched: " + saved.getTrackingNumber(),
                mode.getLabel() + " (" + vehicleNumber + ") en route with " + allocation.getAllocatedQuantity() +
                        " " + item.getUnit() + ". ETA: " + durationMin + " mins.",
                "primary"
        );

        return saved;
    }

    @Transactional
    public TransportMission updateTransportStatus(Long missionId, TransportStatus newStatus, String updatedBy, String statusNotes) {
        TransportMission mission = transportRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));

        mission.setStatus(newStatus);
        if (newStatus == TransportStatus.DELIVERED) {
            mission.setActualDeliveryTime(LocalDateTime.now());
            if (statusNotes != null) {
                mission.setRecipientSignatureNotes(statusNotes);
            }

            // Mark Request & Allocation as DELIVERED & COMPLETED
            EmergencyRequest request = mission.getEmergencyRequest();
            request.setStatus(RequestStatus.DELIVERED);
            requestRepository.save(request);

            ResourceAllocation allocation = mission.getResourceAllocation();
            allocation.setStatus(AllocationStatus.COMPLETED);
            allocationRepository.save(allocation);

            // Deduct from reserved quantity since it's delivered
            ResourceItem item = allocation.getResourceItem();
            item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - allocation.getAllocatedQuantity()));
            resourceItemRepository.save(item);
        }

        TransportMission updated = transportRepository.save(mission);

        notificationService.logAndBroadcast(
                "TRANSPORT",
                updated.getId(),
                newStatus.name(),
                updatedBy,
                "Mission #" + updated.getTrackingNumber() + ": " + newStatus.getLabel(),
                statusNotes != null ? statusNotes : "Transport updated to " + newStatus.getLabel(),
                newStatus == TransportStatus.DELIVERED ? "success" : "info"
        );

        return updated;
    }

    @Transactional
    public boolean verifyDelivery(Long missionId, String inputPin, String recipientNotes, String confirmedBy) {
        TransportMission mission = transportRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));

        EmergencyRequest request = mission.getEmergencyRequest();

        // Check if PIN matches or if empty (override permitted by coordinator)
        if (request.getVerificationPin() != null && !request.getVerificationPin().equals(inputPin)) {
            return false;
        }

        updateTransportStatus(missionId, TransportStatus.DELIVERED, confirmedBy,
                "Verified handover with PIN. Recipient note: " + (recipientNotes != null ? recipientNotes : "Confirmed received."));
        return true;
    }

    public long countActiveMissions() {
        return transportRepository.countByStatus(TransportStatus.IN_TRANSIT) +
                transportRepository.countByStatus(TransportStatus.LOADING) +
                transportRepository.countByStatus(TransportStatus.ASSIGNED);
    }
}
