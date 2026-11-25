package com.resqlink.controller.api;

import com.resqlink.model.ResourceItem;
import com.resqlink.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources API", description = "Endpoints for inventory supplies and depots")
public class ResourceRestController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceRestController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    @Operation(summary = "Get all resources", description = "Retrieves inventory items across all depots")
    public ResponseEntity<List<ResourceItem>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Retrieves a single resource item")
    public ResponseEntity<ResourceItem> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
