package com.resqlink.controller;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.enums.RequestStatus;
import com.resqlink.model.enums.RequestUrgency;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.service.EmergencyRequestService;
import com.resqlink.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final EmergencyRequestService requestService;
    private final IncidentService incidentService;

    @Autowired
    public RequestController(EmergencyRequestService requestService, IncidentService incidentService) {
        this.requestService = requestService;
        this.incidentService = incidentService;
    }

    @GetMapping
    public String listRequests(Model model) {
        model.addAttribute("requests", requestService.getAllRequests());
        model.addAttribute("activeCount", requestService.countActiveRequests());
        return "requests/list";
    }

    @GetMapping("/triage")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String triageQueue(Model model) {
        model.addAttribute("requests", requestService.getPendingTriageRequests());
        return "requests/triage";
    }

    @PostMapping("/triage/recalculate")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String recalculatePriorities(RedirectAttributes redirectAttributes) {
        requestService.recalculateAllPriorities();
        redirectAttributes.addFlashAttribute("successMessage", "Priority scoring re-evaluated successfully!");
        return "redirect:/requests/triage";
    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam(value = "incidentId", required = false) Long incidentId, Model model) {
        model.addAttribute("incidents", incidentService.getActiveIncidents());
        model.addAttribute("categories", ResourceCategory.values());
        model.addAttribute("urgencies", RequestUrgency.values());
        model.addAttribute("selectedIncidentId", incidentId);
        return "requests/create";
    }

    @PostMapping("/create")
    public String submitRequest(@RequestParam Long incidentId,
                                @RequestParam ResourceCategory category,
                                @RequestParam String itemName,
                                @RequestParam Integer requestedQuantity,
                                @RequestParam String unit,
                                @RequestParam RequestUrgency urgency,
                                @RequestParam String requesterName,
                                @RequestParam String requesterPhone,
                                @RequestParam String requesterAddress,
                                @RequestParam Double deliveryLatitude,
                                @RequestParam Double deliveryLongitude,
                                @RequestParam(defaultValue = "1") Integer affectedPeopleCount,
                                @RequestParam(defaultValue = "false") boolean hasInjuredOrInfants,
                                @RequestParam(required = false) String specialInstructions,
                                RedirectAttributes redirectAttributes) {

        Incident incident = incidentService.getIncidentById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        EmergencyRequest request = requestService.submitRequest(
                incident, category, itemName, requestedQuantity, unit, urgency,
                requesterName, requesterPhone, requesterAddress, deliveryLatitude,
                deliveryLongitude, affectedPeopleCount, hasInjuredOrInfants, specialInstructions
        );

        redirectAttributes.addFlashAttribute("successMessage",
                "Your emergency aid request has been registered under Tracking Code: " + request.getTrackingCode() +
                        ". Save your Delivery Verification PIN: " + request.getVerificationPin());

        return "redirect:/tracking/" + request.getTrackingCode();
    }

    @GetMapping("/{id}")
    public String viewRequestDetails(@PathVariable Long id, Model model) {
        EmergencyRequest request = requestService.getRequestById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + id));

        model.addAttribute("request", request);
        return "requests/details";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam RequestStatus status,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        String updatedBy = (auth != null) ? auth.getName() : "Coordinator";
        requestService.updateStatus(id, status, updatedBy);
        redirectAttributes.addFlashAttribute("successMessage", "Request status updated to " + status.getLabel());
        return "redirect:/requests/" + id;
    }
}
