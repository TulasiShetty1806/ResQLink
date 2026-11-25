package com.resqlink.service;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.enums.AllocationStatus;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.repository.EmergencyRequestRepository;
import com.resqlink.repository.ResourceAllocationRepository;
import com.resqlink.repository.ResourceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AllocationRecommendationService {

    private final ResourceItemRepository resourceItemRepository;
    private final ResourceAllocationRepository allocationRepository;
    private final EmergencyRequestRepository requestRepository;
    private final DistanceCalculator distanceCalculator;
    private final NotificationService notificationService;

    @Autowired
    public AllocationRecommendationService(ResourceItemRepository resourceItemRepository,
                                           ResourceAllocationRepository allocationRepository,
                                           EmergencyRequestRepository requestRepository,
                                           DistanceCalculator distanceCalculator,
                                           NotificationService notificationService) {
        this.resourceItemRepository = resourceItemRepository;
        this.allocationRepository = allocationRepository;
        this.requestRepository = requestRepository;
        this.distanceCalculator = distanceCalculator;
        this.notificationService = notificationService;
    }

    /**
     * DTO representing an intelligent allocation recommendation candidate.
     */
    public static class AllocationCandidate {
        private ResourceItem resourceItem;
        private double distanceKm;
        private int suggestedQuantity;
        private double matchScore; // 0 - 100
        private int estimatedTransitMinutes;
        private boolean fullySatisfiesRequest;

        public AllocationCandidate(ResourceItem resourceItem, double distanceKm, int suggestedQuantity,
                                   double matchScore, int estimatedTransitMinutes, boolean fullySatisfiesRequest) {
            this.resourceItem = resourceItem;
            this.distanceKm = distanceKm;
            this.suggestedQuantity = suggestedQuantity;
            this.matchScore = matchScore;
            this.estimatedTransitMinutes = estimatedTransitMinutes;
            this.fullySatisfiesRequest = fullySatisfiesRequest;
        }

        public ResourceItem getResourceItem() { return resourceItem; }
        public double getDistanceKm() { return distanceKm; }
        public int getSuggestedQuantity() { return suggestedQuantity; }
        public double getMatchScore() { return matchScore; }
        public int getEstimatedTransitMinutes() { return estimatedTransitMinutes; }
        public boolean isFullySatisfiesRequest() { return fullySatisfiesRequest; }
    }

    /**
     * Intelligent Resource Finder & Matcher:
     * Evaluates all candidate supply depots against the emergency location.
     * Factors:
     * 1. Proximity score (Haversine distance penalization)
     * 2. Stock sufficiency (can it satisfy the quantity?)
     * 3. Organization reliability/verification
     */
    public List<AllocationCandidate> findRecommendedAllocations(EmergencyRequest request) {
        List<ResourceItem> availableItems = resourceItemRepository.findAvailableForCategory(request.getCategory());
        List<AllocationCandidate> candidates = new ArrayList<>();

        for (ResourceItem item : availableItems) {
            if (item.getAvailableQuantity() <= 0) continue;

            // Distance calculation between depot and delivery point
            double distanceKm = distanceCalculator.calculateDistanceKm(
                    item.getDepotLatitude(), item.getDepotLongitude(),
                    request.getDeliveryLatitude(), request.getDeliveryLongitude()
            );

            int suggestedQty = Math.min(item.getAvailableQuantity(), request.getRequestedQuantity());
            boolean fullyCovers = item.getAvailableQuantity() >= request.getRequestedQuantity();

            // Match score calculation
            // Proximity component: 50 max points (decreases with distance: 0km = 50pts, 100km+ = 0pts)
            double proximityScore = Math.max(0, 50.0 - (distanceKm * 0.5));

            // Sufficiency component: 40 max points
            double sufficiencyRatio = (double) suggestedQty / request.getRequestedQuantity();
            double sufficiencyScore = sufficiencyRatio * 40.0;

            // Organization verification bonus: 10 points
            double orgBonus = (item.getOrganization() != null && item.getOrganization().isVerified()) ? 10.0 : 5.0;

            double totalMatchScore = Math.round((proximityScore + sufficiencyScore + orgBonus) * 10.0) / 10.0;
            totalMatchScore = Math.min(100.0, totalMatchScore);

            int transitMinutes = distanceCalculator.estimateDurationMinutes(distanceKm, null);

            candidates.add(new AllocationCandidate(item, distanceKm, suggestedQty, totalMatchScore, transitMinutes, fullyCovers));
        }

        // Sort descending by highest match score
        candidates.sort(Comparator.comparingDouble(AllocationCandidate::getMatchScore).reversed());
        return candidates;
    }

    @Transactional
    public ResourceAllocation allocateResource(Long requestId, Long resourceItemId, Integer quantity, String allocatedBy, String notes) {
        EmergencyRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        ResourceItem item = resourceItemRepository.findById(resourceItemId)
                .orElseThrow(() -> new IllegalArgumentException("Resource item not found: " + resourceItemId));

        if (item.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock! Available: " + item.getAvailableQuantity() + ", Requested: " + quantity);
        }

        // 1. Deduct stock atomically
        item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
        item.setReservedQuantity(item.getReservedQuantity() + quantity);
        resourceItemRepository.save(item);

        // 2. Create Allocation
        long count = allocationRepository.count() + 1;
        String allocationCode = String.format("ALC-%04d", count);

        ResourceAllocation allocation = new ResourceAllocation(
                allocationCode, request, item, quantity, allocatedBy, notes
        );
        ResourceAllocation saved = allocationRepository.save(allocation);

        // 3. Update Request status to ALLOCATED
        request.setStatus(RequestStatus.ALLOCATED);
        requestRepository.save(request);

        // 4. Log and notify
        notificationService.logAndBroadcast(
                "ALLOCATION",
                saved.getId(),
                "CONFIRMED",
                allocatedBy,
                "Resource Allocated: " + saved.getAllocationCode(),
                quantity + " " + item.getUnit() + " of " + item.getName() + " assigned to Request #" + request.getTrackingCode(),
                "success"
        );

        return saved;
    }

    public List<ResourceAllocation> getAllAllocations() {
        return allocationRepository.findAllByOrderByAllocatedAtDesc();
    }

    public Optional<ResourceAllocation> getAllocationById(Long id) {
        return allocationRepository.findById(id);
    }
}
