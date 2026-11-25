package com.resqlink.controller;

import com.resqlink.model.EmergencyRequest;
import com.resqlink.model.Incident;
import com.resqlink.model.ResourceAllocation;
import com.resqlink.model.TransportMission;
import com.resqlink.repository.ResourceAllocationRepository;
import com.resqlink.service.EmergencyRequestService;
import com.resqlink.service.IncidentService;
import com.resqlink.service.ResourceService;
import com.resqlink.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class TrackingController {

    private final EmergencyRequestService requestService;
    private final TransportService transportService;
    private final ResourceAllocationRepository allocationRepository;
    private final IncidentService incidentService;
    private final ResourceService resourceService;

    @Autowired
    public TrackingController(EmergencyRequestService requestService,
                              TransportService transportService,
                              ResourceAllocationRepository allocationRepository,
                              IncidentService incidentService,
                              ResourceService resourceService) {
        this.requestService = requestService;
        this.transportService = transportService;
        this.allocationRepository = allocationRepository;
        this.incidentService = incidentService;
        this.resourceService = resourceService;
    }

    @GetMapping("/tracking")
    public String trackingSearchPage(@RequestParam(value = "code", required = false) String code) {
        if (code != null && !code.trim().isEmpty()) {
            return "redirect:/tracking/" + code.trim();
        }
        return "tracking/public-track";
    }

    @GetMapping("/tracking/{code}")
    public String trackByCode(@PathVariable String code, Model model) {
        // Try finding by Request Tracking Code
        Optional<EmergencyRequest> reqOpt = requestService.getRequestByTrackingCode(code);
        if (reqOpt.isPresent()) {
            EmergencyRequest request = reqOpt.get();
            List<ResourceAllocation> allocations = allocationRepository.findByEmergencyRequest(request);
            Optional<TransportMission> missionOpt = transportService.getMissionByRequest(request);

            model.addAttribute("request", request);
            model.addAttribute("allocations", allocations);
            model.addAttribute("mission", missionOpt.orElse(null));
            return "tracking/public-track";
        }

        // Try finding by Transport Mission Tracking Number
        Optional<TransportMission> missionOpt = transportService.getMissionByTrackingNumber(code);
        if (missionOpt.isPresent()) {
            TransportMission mission = missionOpt.get();
            EmergencyRequest request = mission.getEmergencyRequest();
            List<ResourceAllocation> allocations = allocationRepository.findByEmergencyRequest(request);

            model.addAttribute("request", request);
            model.addAttribute("allocations", allocations);
            model.addAttribute("mission", mission);
            return "tracking/public-track";
        }

        model.addAttribute("errorMessage", "No emergency request or transport mission found for code: " + code);
        return "tracking/public-track";
    }

    @GetMapping("/tracking/map")
    public String fullScreenMap(Model model) {
        model.addAttribute("incidents", incidentService.getAllIncidents());
        model.addAttribute("resources", resourceService.getAllResources());
        model.addAttribute("requests", requestService.getAllRequests());
        model.addAttribute("missions", transportService.getAllMissions());
        return "tracking/map-view";
    }
}
