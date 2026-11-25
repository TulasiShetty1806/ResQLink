package com.resqlink.controller;

import com.resqlink.model.Organization;
import com.resqlink.model.ResourceItem;
import com.resqlink.model.enums.ResourceCategory;
import com.resqlink.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public String listResources(Model model) {
        model.addAttribute("resources", resourceService.getAllResources());
        model.addAttribute("totalSupplies", resourceService.getTotalAvailableSupplies());
        model.addAttribute("organizations", resourceService.getAllOrganizations());
        return "resources/list";
    }

    @GetMapping("/register")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ORGANIZATION')")
    public String showRegisterForm(Model model) {
        model.addAttribute("categories", ResourceCategory.values());
        model.addAttribute("organizations", resourceService.getAllOrganizations());
        return "resources/register";
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ORGANIZATION')")
    public String registerResource(@RequestParam String name,
                                   @RequestParam ResourceCategory category,
                                   @RequestParam String description,
                                   @RequestParam Integer totalQuantity,
                                   @RequestParam String unit,
                                   @RequestParam String depotLocationName,
                                   @RequestParam Double depotLatitude,
                                   @RequestParam Double depotLongitude,
                                   @RequestParam Long organizationId,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        String registeredBy = (authentication != null) ? authentication.getName() : "Resource Manager";

        ResourceItem item = resourceService.registerResource(
                name, category, description, totalQuantity, unit,
                depotLocationName, depotLatitude, depotLongitude,
                organizationId, expiryDate, registeredBy
        );

        redirectAttributes.addFlashAttribute("successMessage", "Resource " + item.getName() + " (" + item.getResourceCode() + ") successfully added to inventory!");
        return "redirect:/resources";
    }

    @PostMapping("/{id}/restock")
    @PreAuthorize("hasAnyRole('COORDINATOR', 'ORGANIZATION')")
    public String restockResource(@PathVariable Long id,
                                  @RequestParam Integer addedQuantity,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        String updatedBy = (authentication != null) ? authentication.getName() : "Staff";
        resourceService.restockResource(id, addedQuantity, updatedBy);
        redirectAttributes.addFlashAttribute("successMessage", "Stock successfully replenished!");
        return "redirect:/resources";
    }
}
