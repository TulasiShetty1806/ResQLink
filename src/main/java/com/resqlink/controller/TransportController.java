package com.resqlink.controller;

import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.TransportMission;
import com.resqlink.model.enums.TransportMode;
import com.resqlink.model.enums.TransportStatus;
import com.resqlink.service.AllocationRecommendationService;
import com.resqlink.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transport")
public class TransportController {

    private final TransportService transportService;
    private final AllocationRecommendationService allocationService;

    @Autowired
    public TransportController(TransportService transportService,
                               AllocationRecommendationService allocationService) {
        this.transportService = transportService;
        this.allocationService = allocationService;
    }

    @GetMapping
    public String listMissions(Model model) {
        model.addAttribute("missions", transportService.getAllMissions());
        model.addAttribute("activeMissionsCount", transportService.countActiveMissions());
        return "transport/list";
    }

    @GetMapping("/dispatch/{allocationId}")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'LOGISTICS')")
    public String showDispatchForm(@PathVariable Long allocationId, Model model) {
        ResourceAllocation allocation = allocationService.getAllocationById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found: " + allocationId));

        model.addAttribute("allocation", allocation);
        model.addAttribute("transportModes", TransportMode.values());
        return "transport/dispatch";
    }

    @PostMapping("/dispatch")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'LOGISTICS')")
    public String dispatchMission(@RequestParam Long allocationId,
                                  @RequestParam TransportMode transportMode,
                                  @RequestParam String vehicleNumber,
                                  @RequestParam String driverName,
                                  @RequestParam String driverPhone,
                                  @RequestParam(required = false) String routeNotes,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        String dispatchedBy = (authentication != null) ? authentication.getName() : "Logistics Dispatcher";

        TransportMission mission = transportService.dispatchTransport(
                allocationId, transportMode, vehicleNumber, driverName, driverPhone, routeNotes, dispatchedBy
        );

        redirectAttributes.addFlashAttribute("successMessage",
                "Transport Mission " + mission.getTrackingNumber() + " successfully dispatched! Vehicle en route.");

        return "redirect:/transport/" + mission.getId();
    }

    @GetMapping("/{id}")
    public String viewMissionDetails(@PathVariable Long id, Model model) {
        TransportMission mission = transportService.getMissionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + id));

        model.addAttribute("mission", mission);
        model.addAttribute("statuses", TransportStatus.values());
        return "transport/details";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'LOGISTICS')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TransportStatus status,
                               @RequestParam(required = false) String statusNotes,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        String updatedBy = (auth != null) ? auth.getName() : "Logistics";
        transportService.updateTransportStatus(id, status, updatedBy, statusNotes);
        redirectAttributes.addFlashAttribute("successMessage", "Mission status updated to " + status.getLabel());
        return "redirect:/transport/" + id;
    }

    @PostMapping("/{id}/verify")
    public String verifyDelivery(@PathVariable Long id,
                                 @RequestParam String inputPin,
                                 @RequestParam(required = false) String recipientNotes,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        String confirmedBy = (auth != null) ? auth.getName() : "Field Recipient";
        boolean verified = transportService.verifyDelivery(id, inputPin, recipientNotes, confirmedBy);

        if (verified) {
            redirectAttributes.addFlashAttribute("successMessage", "Delivery verified and marked completed! Aid supplies safely transferred.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Verification PIN! Handover verification failed.");
        }

        return "redirect:/transport/" + id;
    }
}
