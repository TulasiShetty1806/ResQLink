package com.resqlink.controller;

import com.resqlink.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
@PreAuthorize("hasRole('COORDINATOR')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public String viewAnalytics(Model model) {
        model.addAttribute("metrics", analyticsService.getDashboardMetrics());
        model.addAttribute("activityLogs", analyticsService.getRecentActivity());
        return "analytics/report";
    }

    @GetMapping("/export-summary")
    public ResponseEntity<byte[]> exportSummaryCsv() {
        Map<String, Object> metrics = analyticsService.getDashboardMetrics();

        StringBuilder csv = new StringBuilder();
        csv.append("ResQLink Disaster Emergency System Summary Report\n");
        csv.append("Metric,Value\n");
        csv.append("Total Incidents,").append(metrics.get("totalIncidents")).append("\n");
        csv.append("Active Incidents,").append(metrics.get("activeIncidents")).append("\n");
        csv.append("Total Requests,").append(metrics.get("totalRequests")).append("\n");
        csv.append("Pending Triage,").append(metrics.get("pendingTriage")).append("\n");
        csv.append("Allocated Requests,").append(metrics.get("allocatedRequests")).append("\n");
        csv.append("In-Transit Missions,").append(metrics.get("inTransitRequests")).append("\n");
        csv.append("Delivered / Fulfilled,").append(metrics.get("deliveredRequests")).append("\n");
        csv.append("Total Available Supplies,").append(metrics.get("totalStock")).append("\n");
        csv.append("Total People Impacted,").append(metrics.get("totalPeopleAffected")).append("\n");

        byte[] output = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resqlink_emergency_summary.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(output);
    }
}
