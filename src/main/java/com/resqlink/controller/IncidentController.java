package com.resqlink.controller;

import com.resqlink.model.Incident;
import com.resqlink.model.enums.IncidentSeverity;
import com.resqlink.model.enums.IncidentStatus;
import com.resqlink.model.enums.IncidentType;
import com.resqlink.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public String listIncidents(Model model) {
        model.addAttribute("incidents", incidentService.getAllIncidents());
        model.addAttribute("activeCount", incidentService.countActiveIncidents());
        return "incidents/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String showCreateForm(Model model) {
        model.addAttribute("incidentTypes", IncidentType.values());
        model.addAttribute("severities", IncidentSeverity.values());
        return "incidents/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String createIncident(@RequestParam String title,
                                 @RequestParam IncidentType incidentType,
                                 @RequestParam IncidentSeverity severity,
                                 @RequestParam String description,
                                 @RequestParam String locationName,
                                 @RequestParam Double latitude,
                                 @RequestParam Double longitude,
                                 @RequestParam(defaultValue = "10.0") Double radiusKm,
                                 @RequestParam(defaultValue = "0") Integer affectedPeopleEstimate,
                                 @RequestParam(defaultValue = "0") Integer casualtiesReported,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String createdBy = (authentication != null) ? authentication.getName() : "System Coordinator";

        Incident incident = incidentService.createIncident(
                title, incidentType, severity, description, locationName,
                latitude, longitude, radiusKm, affectedPeopleEstimate,
                casualtiesReported, createdBy
        );

        redirectAttributes.addFlashAttribute("successMessage", "Emergency Incident " + incident.getIncidentCode() + " successfully declared!");
        return "redirect:/incidents/" + incident.getId();
    }

    @GetMapping("/{id}")
    public String viewIncidentDetails(@PathVariable Long id, Model model) {
        Incident incident = incidentService.getIncidentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));

        model.addAttribute("incident", incident);
        return "incidents/details";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam IncidentStatus status,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String updatedBy = (authentication != null) ? authentication.getName() : "Coordinator";
        incidentService.updateIncidentStatus(id, status, updatedBy);
        redirectAttributes.addFlashAttribute("successMessage", "Incident status updated to " + status.getLabel());
        return "redirect:/incidents/" + id;
    }
}
