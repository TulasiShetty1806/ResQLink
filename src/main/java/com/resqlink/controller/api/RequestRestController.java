package com.resqlink.controller.api;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.service.EmergencyRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Requests API", description = "Endpoints for emergency aid requests and prioritization")
public class RequestRestController {

    private final EmergencyRequestService requestService;

    @Autowired
    public RequestRestController(EmergencyRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Retrieves all aid requests ordered by priority score")
    public ResponseEntity<List<EmergencyRequest>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/prioritized")
    @Operation(summary = "Get prioritized triage requests", description = "Retrieves pending requests ranked by priority score")
    public ResponseEntity<List<EmergencyRequest>> getPrioritizedRequests() {
        return ResponseEntity.ok(requestService.getPendingTriageRequests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request by ID", description = "Retrieves request details by ID")
    public ResponseEntity<EmergencyRequest> getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/track/{code}")
    @Operation(summary = "Track request by code", description = "Public tracking by tracking code")
    public ResponseEntity<EmergencyRequest> getRequestByTrackingCode(@PathVariable String code) {
        return requestService.getRequestByTrackingCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
