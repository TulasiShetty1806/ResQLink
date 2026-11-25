package com.resqlink.controller.api;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.TransportMission;
import com.resqlink.service.AllocationRecommendationService;
import com.resqlink.service.EmergencyRequestService;
import com.resqlink.service.TransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allocations")
@Tag(name = "Allocations & Transport API", description = "Endpoints for resource allocation and transport missions")
public class AllocationRestController {

    private final AllocationRecommendationService allocationService;
    private final EmergencyRequestService requestService;
    private final TransportService transportService;

    @Autowired
    public AllocationRestController(AllocationRecommendationService allocationService,
                                    EmergencyRequestService requestService,
                                    TransportService transportService) {
        this.allocationService = allocationService;
        this.requestService = requestService;
        this.transportService = transportService;
    }

    @GetMapping
    @Operation(summary = "Get all allocations", description = "Retrieves all resource allocations")
    public ResponseEntity<List<ResourceAllocation>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @GetMapping("/recommend/{requestId}")
    @Operation(summary = "Get allocation recommendations", description = "Calculates ranked supply depots for an emergency request")
    public ResponseEntity<?> getRecommendations(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId)
                .map(request -> ResponseEntity.ok(allocationService.findRecommendedAllocations(request)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/missions")
    @Operation(summary = "Get all transport missions", description = "Retrieves all active and completed transport missions")
    public ResponseEntity<List<TransportMission>> getAllMissions() {
        return ResponseEntity.ok(transportService.getAllMissions());
    }
}
