package com.resqlink.controller.api;

import com.resqlink.model.Incident;
import com.resqlink.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents API", description = "Endpoints for emergency disaster incidents management")
public class IncidentRestController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentRestController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Retrieves all reported emergency incidents")
    public ResponseEntity<List<Incident>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active incidents", description = "Retrieves currently active emergency incidents")
    public ResponseEntity<List<Incident>> getActiveIncidents() {
        return ResponseEntity.ok(incidentService.getActiveIncidents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Retrieves a single incident by its unique ID")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
