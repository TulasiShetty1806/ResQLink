package com.resqlink.controller;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.service.AllocationRecommendationService;
import com.resqlink.service.EmergencyRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/allocations")
public class AllocationController {

    private final AllocationRecommendationService allocationService;
    private final EmergencyRequestService requestService;

    @Autowired
    public AllocationController(AllocationRecommendationService allocationService,
                                EmergencyRequestService requestService) {
        this.allocationService = allocationService;
        this.requestService = requestService;
    }

    @GetMapping
    @PreAuthorize("hasRole('COORDINATOR')")
    public String listAllocations(Model model) {
        model.addAttribute("allocations", allocationService.getAllAllocations());
        return "allocations/list";
    }

    @GetMapping("/recommend/{requestId}")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String recommendAllocations(@PathVariable Long requestId, Model model) {
        EmergencyRequest request = requestService.getRequestById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        List<AllocationRecommendationService.AllocationCandidate> candidates =
                allocationService.findRecommendedAllocations(request);

        model.addAttribute("request", request);
        model.addAttribute("candidates", candidates);
        return "allocations/recommend";
    }

    @PostMapping("/allocate")
    @PreAuthorize("hasRole('COORDINATOR')")
    public String allocateResource(@RequestParam Long requestId,
                                   @RequestParam Long resourceItemId,
                                   @RequestParam Integer allocatedQuantity,
                                   @RequestParam(required = false) String notes,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        String allocatedBy = (authentication != null) ? authentication.getName() : "Coordinator";

        try {
            ResourceAllocation allocation = allocationService.allocateResource(
                    requestId, resourceItemId, allocatedQuantity, allocatedBy, notes
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Resource successfully allocated (" + allocation.getAllocationCode() + ")! Ready for logistics dispatch.");

            return "redirect:/transport/dispatch/" + allocation.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/allocations/recommend/" + requestId;
        }
    }
}
