package com.resqlink.controller;

import com.resqlink.model.User;
import com.resqlink.model.enums.UserRole;
import com.resqlink.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final IncidentService incidentService;
    private final EmergencyRequestService requestService;
    private final ResourceService resourceService;
    private final TransportService transportService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    @Autowired
    public WebController(IncidentService incidentService, EmergencyRequestService requestService,
                         ResourceService resourceService, TransportService transportService,
                         AnalyticsService analyticsService, UserService userService) {
        this.incidentService = incidentService;
        this.requestService = requestService;
        this.resourceService = resourceService;
        this.transportService = transportService;
        this.analyticsService = analyticsService;
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("activeIncidents", incidentService.getActiveIncidents());
        model.addAttribute("metrics", analyticsService.getDashboardMetrics());
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been successfully signed out.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("roles", UserRole.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam UserRole role,
                                 @RequestParam(required = false) String organizationName,
                                 Model model) {
        try {
            userService.registerUser(username, password, fullName, email, phone, role, organizationName);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        boolean isCoordinator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINATOR"));
        boolean isOrg = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZATION"));
        boolean isLogistics = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LOGISTICS"));

        if (isCoordinator) {
            return "redirect:/dashboard/coordinator";
        } else if (isOrg) {
            return "redirect:/dashboard/organization";
        } else if (isLogistics) {
            return "redirect:/dashboard/logistics";
        } else {
            return "redirect:/dashboard/citizen";
        }
    }

    @GetMapping("/dashboard/coordinator")
    public String coordinatorDashboard(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        model.addAttribute("metrics", analyticsService.getDashboardMetrics());
        model.addAttribute("activeIncidents", incidentService.getActiveIncidents());
        model.addAttribute("pendingRequests", requestService.getPendingTriageRequests());
        model.addAttribute("recentMissions", transportService.getAllMissions());
        model.addAttribute("recentLogs", analyticsService.getRecentActivity());
        return "dashboard/coordinator";
    }

    @GetMapping("/dashboard/organization")
    public String organizationDashboard(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        model.addAttribute("resources", resourceService.getAllResources());
        model.addAttribute("totalSupplies", resourceService.getTotalAvailableSupplies());
        model.addAttribute("organizations", resourceService.getAllOrganizations());
        return "dashboard/organization";
    }

    @GetMapping("/dashboard/logistics")
    public String logisticsDashboard(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        model.addAttribute("missions", transportService.getAllMissions());
        model.addAttribute("activeMissionsCount", transportService.countActiveMissions());
        return "dashboard/logistics";
    }

    @GetMapping("/dashboard/citizen")
    public String citizenDashboard(Model model, Authentication auth) {
        model.addAttribute("user", auth.getName());
        model.addAttribute("activeIncidents", incidentService.getActiveIncidents());
        model.addAttribute("recentRequests", requestService.getAllRequests());
        return "dashboard/citizen";
    }
}
